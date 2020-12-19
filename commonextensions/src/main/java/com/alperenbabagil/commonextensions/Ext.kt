package com.alperenbabagil.commonextensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.preference.PreferenceManager

import android.util.DisplayMetrics
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

// view ext

fun View.show(){
    this.visibility= View.VISIBLE
}

fun View.hide(){
    this.visibility= View.GONE
}

fun View.invisible(){
    this.visibility= View.INVISIBLE
}

fun View.showHide(isVisible: Boolean){
    this.visibility = if(isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("viewVisibility")
fun View.setVisibility(isVisible: Boolean?){
    isVisible?.let {
        if(it) this.show() else this.hide()
    }
}

@BindingAdapter("viewVisibilityLive")
fun View.setVisibilityLive(isVisibleLiveData: LiveData<Boolean>?){
    isVisibleLiveData?.let {
        it.value?.let {
            if(it) this.show() else this.hide()
        }
    }
}


// view size

fun Int.toDp() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.toPx() : Int  = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Activity.screenWidth(): Int {
    val metrics: DisplayMetrics = DisplayMetrics()
    this.display?.getRealMetrics(metrics)
    return metrics.widthPixels
}

fun Activity.screenHeight(): Int {
    val metrics: DisplayMetrics = DisplayMetrics()
    this.display?.getRealMetrics(metrics)
    return metrics.heightPixels
}

// shared preferences

@SuppressLint("CommitPrefEdits")
fun <T>Context.saveToSharedPreferences(key:String,
                                       value:T,
                                       spEditor: SharedPreferences.Editor?=null) {
    val spe=spEditor ?: PreferenceManager.getDefaultSharedPreferences(this).edit()
    when(value){
        is String -> spe.putString(key,value)
        is Set<*> -> spe.putStringSet(key,value as Set<String>)
        is Int -> spe.putInt(key,value)
        is Long -> spe.putLong(key,value)
        is Float -> spe.putFloat(key,value)
        is Boolean -> spe.putBoolean(key,value)
    }
    spe.apply()
}

inline fun <reified T>Context.getFromSharedPreferences(key:String,
                                                       defaultValue: T,
                                                       sp: SharedPreferences?=null
) : T {
    val spe=sp ?: PreferenceManager.getDefaultSharedPreferences(this)
    return when(defaultValue!!::class){
        String::class -> spe.getString(key, defaultValue as String) as T
        Set::class -> spe.getStringSet(key,defaultValue as Set<String>) as T
        Int::class -> spe.getInt(key,defaultValue as Int) as T
        Long::class -> spe.getLong(key,defaultValue as Long) as T
        Float::class -> spe.getFloat(key,defaultValue as Float) as T
        Boolean::class -> spe.getBoolean(key,defaultValue as Boolean) as T
        else -> defaultValue
    }
}

inline fun <reified T>Context.getFromSharedPreferencesNullable(key:String,
                                                               defaultValue: T? = null,
                                                               sp: SharedPreferences?=null
                                                       ) : T? {
    val spe=sp ?: PreferenceManager.getDefaultSharedPreferences(this)
    return when{
        T::class == String::class -> spe.getString(key, defaultValue?.let{it as String})?.let { it as T }
        T::class == Set::class-> spe.getStringSet(key,defaultValue?.let{it as Set<String>})?.let { it as T }
        T::class == Int::class -> spe.getInt(key,defaultValue?.let{it as Int} ?: 0) as T
        T::class == Long::class -> spe.getLong(key,defaultValue?.let{it as Long} ?: 0) as T
        T::class == Float::class -> spe.getFloat(key,defaultValue?.let{it as Float} ?: 0f) as T
        T::class == Boolean::class -> spe.getBoolean(key,defaultValue?.let{it as Boolean} ?: false) as T
        else -> null
    }
}

// enum

inline fun <reified T : Enum<*>> findEnumConstantFromProperty(predicate: (T) -> Boolean): T =
    T::class.java.enumConstants!!.find(predicate)!!

// lifeCycle

inline fun <T> Activity.getExtra(key:String,
                                 noinline onNotFound: (() -> Unit)?=null,
                                 onFound: (value:T) -> Unit
){
    this.intent?.extras?.let {
        if(it.containsKey(key)) onFound.invoke(it.get(key) as T)
        else onNotFound?.invoke()
    }
}

inline fun <T> Fragment.getArgument(key:String,
                                    noinline onNotFound: (() -> Unit)?=null,
                                    onFound: (value:T) -> Unit
                                    ){
    this.arguments?.let {
        if(it.containsKey(key)) onFound.invoke(it.get(key) as T)
        else onNotFound?.invoke()
    }
}

