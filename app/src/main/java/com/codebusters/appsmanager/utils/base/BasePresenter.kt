package com.codebusters.appsmanager.utils.base

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


open class BasePresenter<V : MvpView> : MvpPresenter<V>() {

    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun addDisposable(disposable : Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}