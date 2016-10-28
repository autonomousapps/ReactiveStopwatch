# Reactive Stopwatch
A sample Android app that demonstrates the usage of several popular libraries, such as Dagger2, Butterknife, RxJava/RxAndroid, and AutoValue; a Model-View-Presenter (MVP) architecture; and techniques for unit- and acceptance-testing the application. Also demonstrated is the use of a hybrid (started & bound) `Service`, which is bound via AIDL. Note that the RxJava usage is non-trivial, but fails at the canonical Retrofit/Github integration :D.

Finally, this sample app targets Nougat, and uses Jack and some Java 8 features.

## Model-View-Presenter
A fundamental goal of this project was to demonstrate the usage of the MVP architectural pattern to create a well-structured, testable and flexible application. The MVP framework used in this demo is hand-rolled and very similar to a version I'm using in production in another project. At a high level, it:

 * Has a thin, reactive View layer. By 'thin', I mean that the View layer doesn't make any decisions on its own. It also sends user events down to the Presenter to evaluate and react to.
 * Has a Presenter layer that encapsulates the presentation logic for the View. The Presenter tells the View what to do, and reacts to UI events as transmitted down from that layer (e.g., button clicks).
 * Has a Model layer that actually 'does things.' In this case, the Model is the [StopwatchImpl](https://github.com/autonomousapps/ReactiveStopwatch/blob/master/app/src/main/java/com/autonomousapps/reactivestopwatch/time/StopwatchImpl.java) class, which implements the [Stopwawtch](https://github.com/autonomousapps/ReactiveStopwatch/blob/master/app/src/main/java/com/autonomousapps/reactivestopwatch/time/Stopwatch.java) interface and provides all the functionality you'd expect from a stopwatch.

Interestingly, the `Stopwatch` interface has a second implementation, [RemoteStopwatch](https://github.com/autonomousapps/ReactiveStopwatch/blob/master/app/src/main/java/com/autonomousapps/reactivestopwatch/time/RemoteStopwatch.java), which essentially serves as a proxy to the `StopwatchImpl` class, which it connects to via the [StopwatchService](https://github.com/autonomousapps/ReactiveStopwatch/blob/master/app/src/main/java/com/autonomousapps/reactivestopwatch/service/StopwatchService.java), which is itself bound via [AIDL](https://developer.android.com/guide/components/aidl.html).

## Test-Driven
While I will not pretend that I practiced pure TDD while developing this demo, I will maintain that, by keeping testability as a foremost concern, it was nevertheless 'test-driven.' For example, despite this being an Android application, I didn't attempt to run the app itself for several weeks while working on it. I started with the `StopwatchImpl` class and its corresponding test suite `StopwatchImplTest`. Once I had that finished to my satisfaction, I moved up the stack to `StopwatchPresenter` and `StopwatchPresenterTest`. Once that was complete, I then moved to `StopwatchFragment` and `StopwatchFragmentTest`. At this point, I still had never attempted to launch the application, other than via the `AndroidJUnitRunner`. When I finally had my unit test suite finished (100% coverage) and green, I finally finished wiring my Fragment to my Activity and launched the app.

It promptly crashed, because of some Rx backpressure issues that weren't apparent in a unit-testing context.

At this point, I took a step back and began working on an acceptance test suite, which ultimately got implemented with [UiAutomator](https://developer.android.com/training/testing/ui-testing/uiautomator-testing.html). The acceptance test suite runs the actual app without any mocking or stubbing -- it's the actual application, running as if a user were interacting with it. Once I solved the backpressure issue, the AT suite demonstrated that the project met all the 'business requirements' for the app, and it gave me the safety net I'd need for further refinement and experimentation.

## Gradle-centric build pipeline
There's a principle in continuous deployment that anyone involved in a project ought to be able to build with the push of a single button. I wanted to achieve this. For most of this project's lifecycle, I was manually running tests -- first pure unit tests, then instrumented unit tests, and finally acceptance tests. If this were a production app, I could then give a build to QA or deploy immediately if we had enough confidence in the acceptance test suite. But it's really too many steps. What I want to be able to do is push a single button (run a single command) to start the build pipeline, at the end of which I have my release APK that can be deployed to QA or the Play Store. I've begun this process with the [tests.gradle](https://github.com/autonomousapps/ReactiveStopwatch/blob/master/gradle/tests.gradle) script, which encapsulates much of the functionality in a series of Gradle tasks. Now all I have to do is run `./gradlew startPipeline`, the test environment will be prepared, and the tests will run in correct sequence (fastest first), stopping quickly if any suite fails. It is still a work in progress.

I could have achieved something like this by setting up Jenkins or another CI service, but I wanted something I could run easily on my machine, without external dependencies.

## Android Nougat
I wanted to experiment with Jack and Java 8 features (without Retrolambda), and so I set minSdk to 24 and ran with it. While working on this project, I have discovered a [number](https://code.google.com/p/android/issues/detail?id=225490) of [issues](https://code.google.com/p/android/issues/detail?id=224466) [relating](https://code.google.com/p/android/issues/detail?id=223549) to [Jack](https://github.com/google/dagger/issues/483). I have concluded that Jack is simply not production-ready at this time, but I did very much enjoy having easy access to Java 8 -- finally.

# Libraries Used
## Production
 * [Dagger2](https://google.github.io/dagger/)
 * [Butterknife](http://jakewharton.github.io/butterknife/)
 * [AutoValue](https://github.com/google/auto/tree/master/value)
 * [RxJava](https://github.com/ReactiveX/RxJava) & [RxAndroid](https://github.com/ReactiveX/RxAndroid)
 
## Testing
 * [Espresso](https://google.github.io/android-testing-support-library/docs/espresso/)
 * [UiAutomator](https://developer.android.com/training/testing/ui-testing/uiautomator-testing.html)
 * [JUnit 4](http://junit.org/junit4/)
 * [Mockito](http://site.mockito.org/)
 * [Cappuccino](https://github.com/metova/Cappuccino)
 * [Awaitility](https://github.com/awaitility/awaitility)

# TODO
 * Add a more interesting implementation of the `TimeTeller` interface.
 * Add a foreground notification for the running service.
 * Add a lockscreen interface.
 * Add Checkstyle and FindBugs.
 * Add JaCoCo.
 * Expand on README or write blog posts or wiki.

# Notes to self:

 * Apparently Espresso will block _after_ an action that causes the main thread to be active. I thought it would _wait to perform_ an action, not wait afterwards. The more you know.
 
# License
>    Licensed under the Apache License, Version 2.0 (the "License");
>   you may not use this file except in compliance with the License.
>   You may obtain a copy of the License at
>
>       http://www.apache.org/licenses/LICENSE-2.0
>
>   Unless required by applicable law or agreed to in writing, software
>   distributed under the License is distributed on an "AS IS" BASIS,
>   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
>   See the License for the specific language governing permissions and
>   limitations under the License.
