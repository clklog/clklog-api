package com.zcunsoft.clklog.api.utils;


import java.time.Duration;  
import java.time.LocalDateTime;  
import java.util.List;  
import java.util.stream.Collectors;  
import java.util.stream.Stream;  
  
public class PreviousHours {  
    public static void main(String[] args) {  
        int numOfHours = 20; // 获取前3小时的数据  
          
        // 获取当前时间  
        LocalDateTime now = LocalDateTime.now();  
          
        // 计算N小时前的时间  
        LocalDateTime startTime = now.minusHours(numOfHours);  
          
        // 创建时间流，这里我们假设每一小时为一个数据点  
        Stream<LocalDateTime> timeStream = Stream.iterate(startTime, date -> date.plusHours(1));  
          
        // 获取前N小时的时间集合  
        List<LocalDateTime> previousHours = timeStream.limit(numOfHours).collect(Collectors.toList());  
          
        // 输出获取到的前N小时的时间集合  
        System.out.println("前" + numOfHours + "小时的时间集合：");  
        for (LocalDateTime dateTime : previousHours) {  
            System.out.println(dateTime);  
        }  
    }  
}
