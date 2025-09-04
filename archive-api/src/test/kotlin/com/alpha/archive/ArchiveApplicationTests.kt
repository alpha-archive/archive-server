package com.alpha.archive

import com.alpha.archive.domain.event.EventImage
import com.alpha.archive.domain.event.EventStatus
import com.alpha.archive.domain.event.PublicEvent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ArchiveApplicationTests {

    fun test(): Unit {

    }
}