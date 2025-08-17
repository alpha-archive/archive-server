package com.alpha.archive

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ArchiveApplicationTests {

	@Test
	fun contextLoads() {
		// Simple test to verify that the application context loads successfully
	}

}
