package me.aaronfang.demos

import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.junit.Assert.*
import org.junit.Test
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


data class TestBean(val info: String, var retryTime: Int, var isSuccess: Boolean = false)

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun testRetry() {
        val atomicInteger = AtomicInteger(0)
        Observable.create<String> {
            it.onNext(System.currentTimeMillis().toString())
            it.onError(Error("error"))
        }
            .doOnSubscribe {
                atomicInteger.incrementAndGet()
            }
//            .retry(3)
            .subscribe(object : Observer<String>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: String) {

                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {
                }

            })
        assertTrue(atomicInteger.toInt() == 4)
    }


    @Test
    fun main1() {
        val bean = TestBean("Test", 0)
        Observable.just(bean)
            .flatMap { bean ->
                bean.retryTime++
                if (bean.retryTime == 6) {
                    bean.isSuccess = true
                }
                if (bean.isSuccess) {
                    Observable.just(bean)
                }
                else {
                    Observable.error(RuntimeException())
                }
            }
            .subscribe (
                {
                    println("成功请求网络,重试次数" + it.retryTime)
                    assertTrue(it.retryTime == 6)
                },
                {
                    println("请求网络失败")
                }
            )
    }
}


class RetryWithTime: Function<Observable<Throwable>, ObservableSource<*>> {
    var current = -1
    // retry delay的时间，以数组的形式表达 单位ms
    private val timeDelay = arrayListOf<Int>(50, 100, 150, 200)

    override fun apply(t: Observable<Throwable>): Observable<*> {
        return t.flatMap { throwableObservable ->
            ++current
            Log.d("RetryWithTime", "retry" + current + String.format("time: %tT", Date()))
            println("retry" + current + String.format("time: %tT", Date()))
            if (current < timeDelay.size) {
                Observable.timer(timeDelay[current].toLong(), TimeUnit.MILLISECONDS)
            } else {
                Observable.error<Any>(throwableObservable)
            }
        }
    }
}










