/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.rj.elasticjob.job.simple;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.rj.elasticjob.fixture.entity.Foo;
import com.rj.elasticjob.fixture.repository.FooRepository;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SpringSimpleJob implements SimpleJob {
    
    @Resource
    private FooRepository fooRepository;
    
    @Override
    public void execute(final ShardingContext shardingContext) {
        System.out.println(String.format("Item: %s |ShardingParameter: %s | Time: %s | Thread: %s | %s",
                shardingContext.getShardingItem(),shardingContext.getShardingParameter() ,
                new SimpleDateFormat("HH:mm:ss").format(new Date()),
                Thread.currentThread().getId(), "SIMPLE"));
        //查询某个分片的信息
        List<Foo> data = fooRepository.findTodoData(shardingContext.getShardingParameter(), 10);
        //批理更新，把Foo.Status.TODO更新为Foo.Status.COMPLETED
        for (Foo each : data) {
            fooRepository.setCompleted(each.getId());
        }
        System.out.println("Thread:" + Thread.currentThread().getId() +" ，更新了数据条数：" + data.size());
        //查看更新后的结果
        List<Foo> newData = fooRepository.findTodoData(shardingContext.getShardingParameter(), 10);
        //查看更新后的日志
//        for (Foo each : data) {
//            System.out.println("Thread:" + Thread.currentThread().getId() +" "+each.toString());
//        }
    }
}
