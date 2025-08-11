package com.alpha.archive.exception.annotation

import com.alpha.archive.exception.ErrorTitle

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@JvmRepeatable(CustomFailResponseAnnotations::class)
annotation class CustomFailResponseAnnotation(val exception: ErrorTitle, val message: String = "")

