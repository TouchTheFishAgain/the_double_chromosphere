package example.ljava.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import example.ljava.entity.vo.SimulationBallMeta;
import example.ljava.entity.vo.SimulationBallRuleForm;
import example.ljava.entity.vo.SimulationBallRuleForm.Range;
import example.ljava.exception.RoutineException;
import example.ljava.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api
@Slf4j
@RequestMapping("simulation")
public class SimulationBallContrller {
    Random random = new Random();
    String randomBallProps[] = new String[] { "m1", "m2", "m3", "m4", "m5", "m6" };
    ObjectMapper omapper = new ObjectMapper();

    @RequestMapping("list")
    @ApiOperation(value = "模拟号码", httpMethod = "GET")
    public Object list(@RequestBody ArrayList<SimulationBallRuleForm> rules) {
        try {
            List<SimulationBallMeta> balls = Lists.newArrayList();
            for (SimulationBallRuleForm rule : rules) {
                balls.addAll(generateRandomBalls(rule));
            }
            return Result.success(balls);
        } catch (RoutineException e) {
            return Result.failure(e.getMessage());
        }
    }

    private List<SimulationBallMeta> generateRandomBalls(SimulationBallRuleForm rule) {
        try {
            List<SimulationBallMeta> simulations = Lists.newArrayList();
            // 单个号码最大重试次数
            int numMaxRetry = 30;
            // 生成值的总次数
            final int numProps = randomBallProps.length;

            while (simulations.size() < rule.getNumber()) {
                SimulationBallMeta simulation = new SimulationBallMeta();
                // 逐个计算号码
                for (
                    int i = 0,
                        // 单个值重试次数
                        numRetry = 0,
                        // 剩余生成次数
                        numRemain = numProps;
                    i < numProps;
                    numRemain = numProps - i
                ) {
                    String prop = randomBallProps[i];

                    Range range = (Range) PropertyUtils.getProperty(rule, prop);
                    Integer minVal = range.getMin();
                    // 如果不是第一个值, 判断上一个值和当前小值中选较大一个
                    if (i > 0) {
                        Integer prevMinVal = (Integer) PropertyUtils.getProperty(simulation, randomBallProps[i - 1]);
                        minVal = prevMinVal.compareTo(minVal) > 0? prevMinVal + 1: minVal;
                    }
                    Integer val = random.nextInt(range.getMax() - minVal) + minVal;
                    simulation.setBallVal(prop, val);
                    // 新生成的值不符规则
                    // if (
                    //     simulation.getNumBiggerNumber() > rule.getNumBiggerNumber()
                    //     || simulation.getNumSmallerNumber() > rule.getNumSmallerNumber()
                    //     || simulation.getNumEvenNumber() > rule.getNumEvenNumber()
                    //     || simulation.getNumOddNumber() > rule.getNumOddNumber()
                    // ) {
                    if (
                        // 小数数量一定要在开始满足
                        // 前几位一定要是 "小数"
                        SimulationBallMeta.isBiggerNumber(val) && rule.getNumSmallerNumber() > i
                        // 要先满足小数数量, 以及小数取值必须给之后几位小数留下空间
                        || simulation.getNumSmallerNumber() < rule.getNumSmallerNumber() && SimulationBallMeta.isBiggerNumber(val + rule.getNumSmallerNumber() - simulation.getNumSmallerNumber())
                        || simulation.getNumBiggerNumber() + numRemain < rule.getNumBiggerNumber()
                        || simulation.getNumSmallerNumber() + numRemain < rule.getNumSmallerNumber()
                        || simulation.getNumEvenNumber() + numRemain < rule.getNumEvenNumber()
                        || simulation.getNumOddNumber() + numRemain < rule.getNumOddNumber()
                    ) {
                        if (numRetry >= numMaxRetry) {
                            log.warn(String.format(
                                "单个值生成重试次数达到 %d 次\n规则 %s\n记录 %s",
                                numRetry,
                                omapper.writeValueAsString(rule),
                                omapper.writeValueAsString(simulation)
                            ));
                            throw new RoutineException("生成失败, 重试次数上限");
                        }
                        numRetry += 1;
                        continue;
                    }
                    // 仍然符合规则, 继续下一个值
                    i += 1;
                    numRetry = 0;
                }

                simulations.add(simulation);
            }
            return simulations;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RoutineException("转换失败");
        }
    }
}
