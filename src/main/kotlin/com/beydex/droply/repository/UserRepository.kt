package com.beydex.droply.repository

import com.beydex.droply.entity.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : ReactiveMongoRepository<User, String>