package com.liang.usercenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class UserCenterApplicationTests {

	@Test
	void testRegex() {
		String username = "example123_用户名"; // 这是你要验证的用户名

		// 定义正则表达式模式，允许数字、字母、下划线和中文字符
		String regexPattern = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";

		// 编译正则表达式模式
		Pattern pattern = Pattern.compile(regexPattern);

		// 创建匹配器
		Matcher matcher = pattern.matcher(username);

		// 进行匹配并输出结果
		if (matcher.matches()) {
			System.out.println("用户名符合要求，不包含特殊字符。");
		} else {
			System.out.println("用户名包含特殊字符或为空。");
		}
	}

	@Test
	void contextLoads() {
	}

}