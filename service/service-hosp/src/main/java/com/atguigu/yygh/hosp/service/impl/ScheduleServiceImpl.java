package com.atguigu.yygh.hosp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.ScheduleCommonRequest;
import com.atguigu.yygh.ScheduleResponse;
import com.atguigu.yygh.common.exception.HospitalException;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.hosp.mapper.ScheduleMapper;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.*;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/22 16:04
 */
@Service
@Slf4j
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {
    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    //上传排期接口
    @Override
    public void save(Map<String, Object> paramMap) {
        String paramMapString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(paramMapString,Schedule.class);
        //根据单位编号 和 排期编号查询
        Schedule scheduleExist = scheduleRepository.
                getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getId());
        //判断
        if(scheduleExist!=null) {
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    //查询排期接口
//    @Override
//    public Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
//        // 创建Pageable对象，设置当前页和每页记录数
//        //0是第一页
//        Pageable pageable = PageRequest.of(page-1,limit);
//        // 创建Example对象
//        Schedule schedule = new Schedule();
//        BeanUtils.copyProperties(scheduleQueryVo,schedule);
//        schedule.setIsDeleted(0);
//        schedule.setStatus(1);
//
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
//                .withIgnoreCase(true);
//        Example<Schedule> example = Example.of(schedule,matcher);
//
//        Page<Schedule> all = scheduleRepository.findAll(example, pageable);
//        return all;
//    }

    @Override
    public PageModel<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        // 查询方法2.0
        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        // 获取未来七天日期集合
        List<Date> dateList = Lists.newArrayList();
        DateTime dateTime =DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(startDate);
        for (int i = 0; i < 7; i++) {
            dateList.add(new DateTime(dateTime.plusDays(i)).toDate());
        }
        Criteria criteria = Criteria.where("status").is(1).and("isDeleted").is(0).and("workDate").in(dateList);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),    // 匹配条件
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );
        AggregationResults<Schedule> aggResults = mongoTemplate.aggregate(agg, Schedule.class, Schedule.class);
        List<Schedule> schedules = aggResults.getMappedResults();

        // 查询总数
        Criteria criteria2 = Criteria.where("status").is(1).and("isDeleted").is(0).and("workDate").in(dateList);
        Aggregation agg2 = Aggregation.newAggregation(
                Aggregation.match(criteria),    // 匹配条件
                Aggregation.count().as("docCount")
        );
        AggregationResults<BookingScheduleRuleVo> aggregate =
                mongoTemplate.aggregate(agg2, Schedule.class, BookingScheduleRuleVo.class);
        BookingScheduleRuleVo mappedResult = aggregate.getUniqueMappedResult();

        PageModel<Schedule> schedulePageModel = new PageModel<>();
        schedulePageModel.setContent(schedules);
        schedulePageModel.setPageNum(page);
        schedulePageModel.setTotalElements(mappedResult.getDocCount());

        return schedulePageModel;
    }

    //删除排期
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        //根据单位编号和排期编号查询信息
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if(schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    //根据单位编号和场地编号，查询排期规则数据
    @Override
    public Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode) {
        //1 根据单位编号 和 场地编号 查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        //2 根据工作日 workDate 进行分组
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),    //匹配条件
                Aggregation.group("workDate")   //分组字段
                .first("workDate").as("workDate")
                //3 统计总预约数和可预约数，就诊医生总人数
                .count().as("docCount")
                .sum("reservedNumber").as("reservedNumber")
                .sum("availableNumber").as("availableNumber"),
                // 排序
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                //4 实现分页
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );

        //调用方法，最终执行
        AggregationResults<BookingScheduleRuleVo> aggResults =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();

        //分组查询的总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();

        //把日期对应星期获取
        for(BookingScheduleRuleVo bookingScheduleRuleVo:bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        //设置最终数据，进行返回
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList",bookingScheduleRuleVoList);
        result.put("total",total);

        //获取单位名称
        String hosName = hospitalService.getHospName(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hosName);
        result.put("baseMap",baseMap);

        return result;
    }

    //根据单位编号 、场地编号和工作日期，查询排期详细信息
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        //根据参数查询mongodb
        List<Schedule> scheduleList = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(
                hoscode, depcode, new DateTime(workDate).toDate());
        //把得到list集合遍历，向设置其他值：单位名称、场地名称、日期对应星期
        scheduleList.stream().forEach(item->{
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    @Override
    public List<Schedule> getDetailSchedule2(String hoscode, String depcode, String workDate, String docName) {
        // 根据参数查询mongodb
        List<Schedule> scheduleList = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDateAndDocname(
                hoscode, depcode, new DateTime(workDate).toDate(), docName);
        // 过滤已过预约时间场地

        // 把得到list集合遍历，向设置其他值：单位名称、场地名称、日期对应星期
        List<Schedule> result = scheduleList.stream().filter(item -> {
            String date = new SimpleDateFormat("yyyy-MM-dd#HH：mm").format(new Date());
            String time = date.split("#")[1];
            String scheduleDate = date.split("#")[0];
            String areaValidTime = item.getSkill().split("-")[0].trim();
            if (scheduleDate.compareTo(workDate) < 0) {
                return true;
            } else if (areaValidTime.split("：")[0].length() < 2) {
                areaValidTime = "0" + areaValidTime;
            }
            return areaValidTime.compareTo(time) >= 0;
        }).collect(Collectors.toList());

        result.stream().forEach(item->{
            this.packageSchedule(item);
        });
        return result;
    }

    //获取可预约排期数据
    @Override
    public Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode) {
        Map<String, Object> result = new HashMap<>();

        //1. 根据单位id获取获取预约规则（预约周期，放号时间）
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        if (hospital == null) {
            throw new HospitalException(ResultCodeEnum.DATA_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();

        //2. 获取可预约日期（一个周期）的数据（分页）
        IPage iPage = this.getListDate(page, limit, bookingRule);
        //这里只获取了一页的数据
        List<Date> dateList = iPage.getRecords();

        //3. 获取可预约日期里面剩余的预约数
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode)
                .and("workDate").in(dateList);

        //.first.as：如果 group 出来一组不止一个，按照workDate这个变量选 worDate 小的那个
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );
        //这里查出来的 BookingScheduleRuleVo 有可能是断天数的，因为有天无场地
        AggregationResults<BookingScheduleRuleVo> aggregateResult =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> scheduleRuleVoList = aggregateResult.getMappedResults();
        //这一步很重要！原来get 出来的list 是内部 arraylist，不能操作的
        scheduleRuleVoList = new ArrayList<>(scheduleRuleVoList);

        //按照 workDate 排序
        scheduleRuleVoList = scheduleRuleVoList.stream()
                .sorted(Comparator.comparing(BookingScheduleRuleVo::getWorkDate))
                .collect(Collectors.toList());


        //4. 将 BookingScheduleRuleVo 按照 dateList 天数补齐 并且设置预约规则
        for (int i = 0, len = dateList.size(); i < len; i++) {
            Date date = dateList.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = null;
            if (scheduleRuleVoList.size() > i) {
                bookingScheduleRuleVo = scheduleRuleVoList.get(i);
            }

            //判断最后是否需要加入到队列
            boolean flag = false;
            //如果 date 天没有场地可预约 或者完全没有可预约的
            if (scheduleRuleVoList.size() <= i || date.compareTo(bookingScheduleRuleVo.getWorkDate()) != 0) {
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //就诊医生人数
                bookingScheduleRuleVo.setDocCount(0);
                //场地剩余预约数  -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
                flag = true;
            }

            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //计算当前预约日期对应星期
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            //最后一页最后一条记录为即将预约   状态 0：正常；1：即将放号； -1：当天已停止挂号
            if(i == dateList.size() - 1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            // 当天预约如果过了停号时间，不能预约。下线停号
//            if(i == 0 && page == 1) {
//                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
//                if(stopTime.isBeforeNow()) {
//                    //停止预约
//                    bookingScheduleRuleVo.setStatus(-1);
//                }
//            }
            if (flag) scheduleRuleVoList.add(i, bookingScheduleRuleVo);
        }

        //可预约日期规则数据
        result.put("bookingScheduleList", scheduleRuleVoList);
        result.put("total", iPage.getTotal());

        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //单位名称
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //场地
        Department department =departmentService.getDepartment(hoscode, depcode);
        //大场地名称
        baseMap.put("bigname", department.getBigname());
        //场地名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }

    //根据排期id获取排期详细数据
    @Override
    public Schedule getScheduleId(String scheduleId) {
        //TODO 这里有点问题；。。。。。。 有 id 格式不同的传进来了
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        return this.packageSchedule(schedule);
    }

    //根据排期id获取预约下单数据
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ,
            timeout = 2, rollbackFor = RuntimeException.class)
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        //1. 获取排期信息
        Schedule schedule = this.getScheduleId(scheduleId);
        if (schedule == null) {
            throw new HospitalException(ResultCodeEnum.PARAM_ERROR);
        }

        //2. 获取单位规则信息
        Hospital hospital = hospitalService.getByHoscode(schedule.getHoscode());
        if (hospital == null) throw new HospitalException(ResultCodeEnum.PARAM_ERROR);
        BookingRule bookingRule = hospital.getBookingRule();
        if (bookingRule == null) throw new HospitalException(ResultCodeEnum.PARAM_ERROR);

        //3. 将数据设置到 schedule
        scheduleOrderVo.setHoscode(hospital.getHoscode());
        scheduleOrderVo.setHosname(hospital.getHosname());
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        scheduleOrderVo.setDepname(departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());

        //退号截止天数（如：就诊前一天为-1，当天为0）
        int quitDay = bookingRule.getQuitDay();
        DateTime quitTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(), bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(quitTime.toDate());

        //当天预约开始时间（每天都从这个点开始预约）
        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());

        //当天停止预约挂号时间
        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStartTime(startTime.toDate());

        //预约截止时间
        DateTime endTime = this.getDateTime(new DateTime().plusDays(bookingRule.getCycle()).toDate(), bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());

        return scheduleOrderVo;
    }

    //更新排期数据 用于mq
    @Override
    public void update(Schedule schedule) {
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }

    @Override
    public void updateSchedule(Schedule schedule) {
        if (schedule.getId() == null) {
            throw new HospitalException(ResultCodeEnum.DATA_ERROR);
        }
        Schedule sc = scheduleRepository.
                getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getId());
        sc.setReservedNumber(schedule.getReservedNumber());
        sc.setAvailableNumber(schedule.getAvailableNumber());
        sc.setAmount(schedule.getAmount());
        sc.setSkill(schedule.getSkill());
        this.update(sc);
    }

    @Override
    public ScheduleResponse<ScheduleDocResponse> getDocName(ScheduleCommonRequest request) {
        if (null == request) {
            return null;
        }
        ScheduleResponse<ScheduleDocResponse> scResponse = new ScheduleResponse<>();
        scResponse.setData(new ScheduleDocResponse());
        int limit = 100;
        int page = request.getPage();
        try {
//            List<String> docName = Lists.newArrayList();
//            // 分页查询，条件为
//            Sort sort = Sort.by(Sort.Direction.ASC, "docName");
//            Pageable pageable = PageRequest.of(page, limit, sort);
//            ExampleMatcher exampleMatcher = ExampleMatcher.matching()
//                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).withIgnoreCase(true);
//            Schedule schedule = new Schedule();
//            // 条件为 depcode hoscode date
//            schedule.setWorkDate(new DateTime(request.getDate()).toDate());
//            schedule.setDepcode(request.getDepcode());
//            schedule.setHoscode(request.getHosode());
//            Example<Schedule> example = Example.of(schedule, exampleMatcher);
//            PageModel<Schedule> schedules = scheduleRepository.findAll(example, pageable);

            // 第二种查询方法，只查询不分页
            List<Schedule> records = scheduleRepository.findDistinctByHoscodeAndDepcodeAndWorkDate(
                    request.getHosode(), request.getDepcode(), new DateTime(request.getDate()).toDate());
            // 把得到list集合遍历，向设置其他值：单位名称、场地名称、日期对应星期
            List<Schedule> result = records.stream().filter(item -> {

                String[] date = new SimpleDateFormat("yyyy-MM-dd#HH：mm").format(new Date()).split("#");
                String areaValidTime = item.getSkill().split("-")[0].trim();
                if (date[0].compareTo(request.getDate()) < 0) {
                    return true;
                } else if (areaValidTime.split("：")[0].length() < 2) {
                    areaValidTime = "0" + areaValidTime;
                }
                return areaValidTime.compareTo(date[1]) >= 0;
            }).collect(Collectors.toList());

            if (result.size() == 0) {
                return scResponse;
            }
            // 第一页是 1
            if (page <= 0) {
                page = 1;
            }
            int start = (page - 1) * limit;
            if (start >= result.size()) {
                log.error("getDocName 分页长度超出");
                return null;
            }
            start = start < 0 ? 0 : start;
            // 结果转换，先去重
            List<String> docNames = result.stream().map(Schedule::getDocname).distinct().collect(Collectors.toList());
            // 分页
            List<String> collect = docNames.stream().skip(start).limit(limit).collect(Collectors.toList());
            List<Schedule> res = Lists.newArrayList();
            collect.stream().forEach(sc -> {
                Schedule s = new Schedule();
                s.setDocname(sc);
                res.add(s);
            });

            int total = docNames.size() / limit + (docNames.size() % limit > 0 ? 1 : 0);
            scResponse.getData().setCurrentPage(page);
            scResponse.getData().setLimit(limit);
            scResponse.getData().setTotal(total);
            scResponse.getData().setScheduleDocList(res);
        } catch (Exception e) {
            log.error("getDocName error", e);
            return null;
        }
        return scResponse;
    }

    //获取可预约日期分页数据，根据周期分页显示
    private IPage getListDate(Integer page, Integer limit, BookingRule bookingRule) {
        //获取当天放号时间 年 月 日 小时 分钟
        DateTime releaseTime = getDateTime(new Date(), bookingRule.getReleaseTime());
        //获取预约周期（预约周期是指可以提前多少天内预约，比如 预约周期是 3，最多只能提前三天预约
        Integer cycle = bookingRule.getCycle();
        //当前放号时间过后，预约周期从后一天开始计算，周期+1
        if (releaseTime.isBeforeNow()) {
            cycle += 1;
        }
        //获取可预约所有日期，最后一天显示即将放号
        List<Date> dateList = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            //转换日期格式，这是真正要显示的时间
            DateTime curdateTime = new DateTime().plusDays(i);
            String dateString = curdateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }
        //预约周期大于 7天（前端最大显示数），超过七天分页
        List<Date> pageDateList = new ArrayList<>();
        int start = (page - 1) * limit;
        int end = page * limit;
        //没超过 7 天
        if (end > dateList.size()) {
            end = dateList.size();
        }
        for (int i = start; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }
        //如果可以显示数大于7，进行分页
        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, limit, dateList.size());
        iPage.setRecords(pageDateList);
        return iPage;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）字符串 转换为DateTime格式
     * date 获取年月日
     * timeString 获取时分
     * 拼起来
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    //封装排期详情其他值 单位名称、场地名称、日期对应星期
    private Schedule packageSchedule(Schedule schedule) {
        //设置单位名称
        schedule.getParam().put("hosname",hospitalService.getHospName(schedule.getHoscode()));
        //设置场地名称
        schedule.getParam().put("depname", departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
        return schedule;
    }

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
