package ru.droply.test

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import ru.droply.DroplyApplication

@Transactional
@ActiveProfiles("test")
@SpringBootTest(classes = [DroplyApplication::class])
class DroplyTest