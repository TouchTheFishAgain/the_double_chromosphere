package example.ljava.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.DispatcherServlet;

import example.ljava.util.Result;
import lombok.Setter;

@Controller
public class IndexController {
    @Setter
    @Inject
    private ErrorAttributes errorAttributes;

    @Inject
    WebRequest request;

    @ResponseBody
    @RequestMapping("index")
    public Object index(Model model) {
        return "OK";
    }

    @RequestMapping("message")
    @ResponseBody
    public Object error(Model model) {
        Throwable e = this.errorAttributes.getError(request);
        if (e == null) {
            Object eObject = request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE, WebRequest.SCOPE_REQUEST);
            if (eObject != null && eObject instanceof Throwable) {
                e = (Throwable) eObject;
            }
        }
        String msg = "失败";
        List<String> data = null;
        if (e instanceof BindException) {
            BindException bindErr = (BindException) e;
            data = bindErr.getBindingResult().getFieldErrors().stream()
                .map(i -> i.getField() + " " + i.getDefaultMessage())
                .collect(Collectors.toList());
            if (data.size() > 0) msg = data.get(0);
        } else if (e != null) {
            msg = e.getMessage();
        }
        return Result.failure(msg, data);
    }
}
