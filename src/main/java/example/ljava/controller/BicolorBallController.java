package example.ljava.controller;

import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

import example.ljava.entity.BicolorBall;
import example.ljava.repository.BicolorBallRepository;
import example.ljava.util.Result;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("issue")
@Validated
public class BicolorBallController {

    @Inject
    BicolorBallRepository ballresp;

    @ApiOperation("期号列表")
    @RequestMapping("list")
    @ResponseBody
    public Object list(Model model, @Min(1) @RequestParam(defaultValue = "15") Integer pageSize,
            @Min(1) @Max(250) @RequestParam(defaultValue = "1") Integer pageNumber, String stage) {
        Page<BicolorBall> pagination = ballresp
                .findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.DESC, "id")));
        return Result.success(pagination.toList());
    }
}
