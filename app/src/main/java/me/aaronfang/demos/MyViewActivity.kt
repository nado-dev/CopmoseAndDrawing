package me.aaronfang.demos

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

data class TestBean(val info: String, var retryTime: Int, var isSuccess: Boolean = false)

class MyViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_view)

        // 测试
        doTest()
    }

    @SuppressLint("CheckResult")
    private fun doTest() {
        val bean = TestBean("Test", 0)
        Observable.just(bean)
            .flatMap { bean ->
                bean.retryTime++
                if (bean.retryTime == 4) {
                    bean.isSuccess = true
                }
                if (bean.isSuccess) {
                    Observable.just(bean)
                }
                else {
                    Observable.error(RuntimeException())
                }
            }
            .retryWithTime(arrayListOf(50, 100, 150, 200), TimeUnit.MILLISECONDS)
            .subscribe (
                {
                    println("成功请求网络,重试次数" + it.retryTime)
                },
                {
                    println("请求网络失败")
                }
            )
    }
}

class RetryWithTimeActual(private val timeIntervalList: ArrayList<Long>, private val timeUnit: TimeUnit)
    : Function<Observable<Throwable>, ObservableSource<*>> {
    private var current = -1
    override fun apply(t: Observable<Throwable>): Observable<*> {
        return t.flatMap { throwableObservable ->
            ++current
            Log.d("RetryWithTime", "retry $current time(s) at ${String.format(" time: %t", Date())}")
            if (current < timeIntervalList.size) {
                Observable.timer(timeIntervalList[current],timeUnit)
            } else {
                Observable.error<Any>(throwableObservable)
            }
        }
    }
}

/**
 * 根据传入间隔时间的 List 进行 retry
 */
fun <T> Observable<T>.retryWithTime(timeIntervalList: ArrayList<Long>, timeUnit: TimeUnit): Observable<T> =
    retryWhen(RetryWithTimeActual(timeIntervalList, timeUnit))

/**
 * 根据重传次数 时间间隔 时间单位进行 retry
 */
fun <T> Observable<T>.retryWithTime(retryTimes: Int, timeInterval: Long, timeUnit: TimeUnit): Observable<T> {
    val list = arrayListOf<Long>()
    repeat(retryTimes){
        list.add(timeInterval)
    }
    return retryWhen(RetryWithTimeActual(list, timeUnit))
}




