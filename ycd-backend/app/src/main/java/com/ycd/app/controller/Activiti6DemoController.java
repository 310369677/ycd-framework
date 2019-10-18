package com.ycd.app.controller;

import com.ycd.common.Result;
import com.ycd.common.context.UserContext;
import com.ycd.common.web.AbstractController;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("activiti6")
public class Activiti6DemoController extends AbstractController {

    //模拟一条数据
    Map<String, String> table = new HashMap<>();


    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    AtomicLong count = new AtomicLong(System.currentTimeMillis());

    @PostMapping("startProcess")
    public Result<String> startProcess() {
        table.put("id", count.getAndIncrement() + "");
        table.put("name", "烧鸡");
        Map<String, Object> variable = new HashMap<>();
        variable.putIfAbsent("ASSIGNEE_USER", UserContext.getCurrentUser().getId() + "");
        runtimeService.startProcessInstanceByKey("myProcess_1", "myProcess_1:" + table.get("id"), variable);
        return Result.ok();
    }

    //查询当前用户的代办事项
    @PostMapping("waitEvent")
    public Result<List<Task>> waitEvent() {
        long id = UserContext.getCurrentUser().getId();
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> list = taskQuery.taskAssignee(id + "").list();
        return Result.ok(list);
    }
}
