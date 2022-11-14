## Immersionlayout
## 实现沉浸式布局+下拉刷新+上拉加载+滚动布局的效果

![滚动后toolbar渐变，toolbar内控件字体颜色和图标同步改变](image/immersion_demo1.gif "github")


## 简化沉浸式布局的实现，只需三步，即可实现沉浸式布局效果：

### 一、在layout>xml布局文件中使用ImmersionScrollRefreshLayout

```
<com.youngwm.immersion.ImmersionScrollRefreshLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/immersionLayout">
    
</com.youngwm.immersion.ImmersionScrollRefreshLayout>
```

### 二、在AndroidManifest.xml中对应的activity主题设置为NoActionBar即可，例如：

```
<activity android:name=".demo1.Demo1Activity"
          android:theme="@style/Theme.AppCompat.Light.NoActionBar"/>
```

### 三、在Activity中对调用ImmersionScrollRefreshLayout.enableImmersion()函数即可

```
val immersionLayout = findViewById<ImmersionScrollRefreshLayout>(R.id.immersionLayout)
        immersionLayout.apply {
            enableImmersion(this@Demo1Activity)
        }
```

以上，就实现沉浸式布局的效果。


常见问题:
系统栏无法进入沉浸式，请检查布局文件是否使用 fitsSystemWindows = true
