package example.ljava.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.querydsl.core.types.Predicate;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import example.ljava.entity.po.BicolorBall;
import example.ljava.entity.po.QBicolorBall;
import example.ljava.entity.vo.AnalysisBallForm;
import example.ljava.repository.BicolorBallRepository;
import example.ljava.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Api
@RestController
@Valid
@Slf4j
@RequestMapping("analysis")
class AnalysisBallController {
    final String[] ballNumProps = new String[] { "num1", "num2", "num3", "num4", "num5", "num6", "bnum" };

    @Inject
    @Setter
    BicolorBallRepository ballresp;

    @RequestMapping("get")
    @ApiOperation(httpMethod = "GET", value = "统计一百期号码出现情况归类")
    public Object get(
        @NotEmpty String stageStart,
        @NotEmpty String stageEnd
    ) {
        QBicolorBall qball = QBicolorBall.bicolorBall;
        Predicate predicate = qball.stage.goe(stageStart)
            .and(qball.stage.loe(stageEnd));

        long ballCount = ballresp.count(predicate);
        if (ballCount != 100) {
            return Result.failure(String.format("期数%s 100", ballCount > 100? "大于": "小于"));
        }

        Iterable<BicolorBall> itr = ballresp.findAll(predicate);
        Set<String> stages = Sets.newHashSet();
        Map<Integer, Integer> count = Maps.newHashMap();
        try {
            // 整理数据到结构中
            for (BicolorBall ball : itr) {
                stages.add(ball.getStage());
                for (String prop : ballNumProps) {
                    Integer val = Integer.parseInt(BeanUtils.getProperty(ball, prop));
                    count.put(val, Integer.sum(count.getOrDefault(val, 0), 1));
                }
            }

            // 计算展示值
            AnalysisBallForm analysis = new AnalysisBallForm();
            analysis.setCold(Lists.newArrayList());
            analysis.setSupercold(Lists.newArrayList());
            analysis.setSuperheat(Lists.newArrayList());
            analysis.setHeat(Lists.newArrayList());
            analysis.setWarm(Lists.newArrayList());
            analysis.setHundred(new String[] { stageStart, stageEnd });

            count.forEach((k, v) -> {
                if (v.compareTo(26) >= 0) {
                    analysis.getSuperheat().add(k);
                    return;
                }
                if (v.compareTo(20) >= 0) {
                    analysis.getHeat().add(k);
                    return;
                }
                if (v.compareTo(16) >= 0) {
                    analysis.getWarm().add(k);
                    return;
                }
                if (v.compareTo(14) >= 0) {
                    analysis.getCold().add(k);
                    return;
                }
                analysis.getSupercold().add(k);
            });

            return Result.success(analysis);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error(e.getMessage(), e);
            return Result.failure();
        }
    }
}