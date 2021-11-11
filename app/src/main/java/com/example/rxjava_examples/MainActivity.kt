package com.example.rxjava_examples

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val compositeDisposable = CompositeDisposable()
        val button1: Button = findViewById(R.id.button)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)
        val button4: Button = findViewById(R.id.button4)


        button1.setOnClickListener {
            compositeDisposable.add(multiplyValueAndFilterByValue())
        }

        button2.setOnClickListener {
            compositeDisposable.add(mergeAndSkipValues())
        }

        button3.setOnClickListener {
            compositeDisposable.add(zipValues())
        }

        button4.setOnClickListener {
            compositeDisposable.add(timestampValues())
        }
    }

    private fun createObservable(start: Int = 1, end: Int = 100): Observable<Int> {
        return Observable.range(start, end)
    }

    private fun multiplyValueAndFilterByValue(): Disposable {
        val result = createObservable(1, 10)
        return result
            .map {
                Thread.sleep(100)
                it * 3
            }
            .filter { it > 5 }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("TAG", it.toString())
            }, {
                Log.d("TAG", it.toString())
            })
    }

    private fun mergeAndSkipValues(): Disposable {
        val result1 = Observable.range(1, 10)
        val result2 = Observable.range(11, 100)
        return Observable
            .merge(result1, result2)
            .skip(50)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("TAG", it.toString())
            }, {
                Log.d("TAG", it.toString())
            })
    }

    private fun zipValues(): Disposable {
        val result1 = Observable.just("Hello", "World", "My", "Name", "Is")
        val result2 = Observable.range(11, 100)
        return Observable
            .zip(result1, result2, { res1, res2 ->
                res1 + res2.toString()
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("TAG", it.toString())
            }, {
                Log.d("TAG", it.toString())
            })
    }

    private fun timestampValues(): Disposable {
        val result = createObservable(1, 10)
        return result
            .timestamp()
            .map {
                Thread.sleep(100)
                val timeAsDate = convertLongToTime(it.time())
                Pair(timeAsDate, it.value() * 3)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("TAG", "Timestamp: ${it.first} | Value: ${it.second}")
            }, {
                Log.d("TAG", it.toString())
            })
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm ss.SSS")
        return format.format(date)
    }
}