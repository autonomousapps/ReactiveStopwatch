package com.autonomousapps.reactivestopwatch.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Apply this annotation to all 'Commit-stage' instrumentation test classes. Pass this as
 * an argument to the test runner to differentiate between commit-stage and acceptance tests.
 *
 * Note: In a past project, I used {@code RetentionPolicy.CLASS}, but that doesn't seem to work
 * any longer.
 */
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.TYPE})
public @interface CommitStage {
}