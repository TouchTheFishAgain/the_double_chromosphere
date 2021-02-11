package example.ljava.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import example.ljava.entity.po.BicolorBall;
import example.ljava.entity.po.QBicolorBall;
import example.ljava.entity.vo.BicolorBallForm;
import example.ljava.repository.BicolorBallRepository;
import example.ljava.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api
@Controller
@RequestMapping("issue")
@Validated
@Slf4j
public class BicolorBallController {

    @Inject
    BicolorBallRepository ballresp;

    @ApiOperation(value = "期号列表", httpMethod = "GET")
    @RequestMapping("list")
    @ResponseBody
    public Object list(Model model, @Min(1) @Max(250) @RequestParam(defaultValue = "15") Integer pageSize,
            @Min(0) @RequestParam(defaultValue = "0") Integer pageNumber, String stage, Long id) {
        QBicolorBall qBicolorBall = QBicolorBall.bicolorBall;

        List<BooleanExpression> conditions = Lists
                .newArrayList(
                    id != null ? qBicolorBall.id.eq(id) : null,
                    stage != null ? qBicolorBall.stage.equalsIgnoreCase(stage) : null)
                .stream().filter(m -> m != null).collect(Collectors.toList());

        Predicate query = conditions.size() > 0 ? conditions.stream().skip(1).reduce(conditions.get(0),
                (BooleanExpression i, BooleanExpression c) -> c.and(i)) : null;

        Page<BicolorBall> pagination = query != null
                ? ballresp.findAll(query, PageRequest.of(pageNumber, pageSize, Sort.by(Direction.DESC, "id")))
                : ballresp.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.DESC, "id")));

        return Result.success(pagination);
    }

    @ApiOperation(value = "录入期号", httpMethod = "POST")
    @RequestMapping("create")
    @ResponseBody
    public Object create(@Validated({ BicolorBallForm.Create.class }) BicolorBallForm form) {
        Optional<BicolorBall> optionalBall = ballresp.findFirstByStageIgnoreCase(form.getStage());
        if (optionalBall.isPresent()) {
            return Result.failure("期号已经录入");
        }
        BicolorBall ball = new BicolorBall();
        try {
            BeanUtils.copyProperties(ball, form);
            ballresp.save(ball);
            return Result.success(ball);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
            return Result.failure();
        }
    }

    @ApiOperation(value = "更新期号", httpMethod = "POST")
    @RequestMapping("update")
    @ResponseBody
    public Object update(@Validated({ BicolorBallForm.Update.class }) BicolorBallForm form) {
        Optional<BicolorBall> optionalBall = ballresp.findById(form.getId());
        if (!optionalBall.isPresent()) {
            return Result.failure(String.format("记录 %d 不存在", form.getId()));
        }

        BicolorBall ball = optionalBall.get();
        try {
            // 部分更新
            BeanUtils.describe(form).forEach((String key, String val) -> {
                if (val == null) return;
                try {
                    BeanUtils.setProperty(ball, key, val);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error(e.getMessage(), e);
                }
            });
            ballresp.save(ball);
            return Result.success(ball);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException  e) {
            log.error(e.getMessage(), e);
            return Result.failure();
        }
    }

    @ApiOperation(value = "删除期号", httpMethod = "DELETE")
    @RequestMapping("delete")
    @ResponseBody
    public Object delete(@NotNull Long id) {
        Optional<BicolorBall> optionalBall = ballresp.findById(id);
        if (!optionalBall.isPresent()) {
            return Result.failure(String.format("记录 %d 不存在", id));
        }

        ballresp.deleteById(id);
        return Result.success(optionalBall.get());
    }
}
