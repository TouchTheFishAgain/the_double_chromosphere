package example.ljava.controller;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import example.ljava.entity.BicolorBall;
import example.ljava.repository.BicolorBallRepository;

@Controller
@RequestMapping("issue")
public class BicolorBallController {

    @Inject
    BicolorBallRepository ballresp;

    @RequestMapping("list")
    @ResponseBody
    public Object list(Model model, Integer pageSize, Integer pageNumber) {
        if (pageNumber == null)
            pageNumber = 1;
        if (pageSize == null)
            pageSize = 15;

        Page<BicolorBall> pagination = ballresp
                .findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.DESC, "id")));
        return pagination.toList();
    }
}
