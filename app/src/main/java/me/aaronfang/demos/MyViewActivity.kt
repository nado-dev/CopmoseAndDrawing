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

typealias LogDelegator = Log

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
                // 第 n 次才成功，那么前面 n-1 次都是失败的
                if (bean.retryTime == 6) {
                    bean.isSuccess = true
                }
                if (bean.isSuccess) {
                    Observable.just(bean)
                }
                else {
                    Observable.error(Exception(bean.retryTime.toString()))
                }
            }
            .retryWithTime()
            .subscribe (
                {
                    Log.d("RetryWithTime", "成功请求网络,失败次数" + (it.retryTime - 1) + ",共${it.retryTime}次")
                },
                {
                    Log.d("RetryWithTime", "请求网络失败，错误信息 "+it.message)
                }
            )
    }
}

class RetryWithTimeActual(
    private var retryTimes: Int,
    private val timeIntervalList: ArrayList<Long>,
    private val timeUnit: TimeUnit)
    : Function<Observable<Throwable>, ObservableSource<*>> {
    private var current = -1
    override fun apply(t: Observable<Throwable>): Observable<*> {
        return t.flatMap { throwableObservable ->
            LogDelegator.d("RetryWithTime", "retry ${current + 2} time(s)")
            if (retryTimes > timeIntervalList.size) {
                retryTimes = timeIntervalList.size
            }
            // 这里可以增加判断具体类型的逻辑
            if (++current < retryTimes) {
                Observable.timer(timeIntervalList[current], timeUnit)
            } else {
                Observable.error<Any>(throwableObservable)
            }
        }
    }
}

/**
 * 根据传入间隔时间的 List 进行 retry
 * @param retryTimes 默认进行 3 次失败重传
 * @param timeIntervalList 默认重传时间间隔为 50ms、150ms、350ms、600ms、1200ms
 * @param timeUnit 时间单位 默认ms
 */
fun <T> Observable<T>.retryWithTime(
    retryTimes: Int = 3,
    timeIntervalList: ArrayList<Long> = arrayListOf(50, 150, 350, 600, 1200),
    timeUnit: TimeUnit = TimeUnit.MILLISECONDS
): Observable<T> =
    retryWhen(RetryWithTimeActual(retryTimes, timeIntervalList, timeUnit))

/**
 * 根据重传次数和固定时间间隔进行 retry
 */
fun <T> Observable<T>.retryWithTime(retryTimes: Int, timeInterval: Long, timeUnit: TimeUnit): Observable<T> {
    val list = arrayListOf<Long>()
    repeat(retryTimes){
        list.add(timeInterval)
    }
    return retryWhen(RetryWithTimeActual(retryTimes, list, timeUnit))
}




