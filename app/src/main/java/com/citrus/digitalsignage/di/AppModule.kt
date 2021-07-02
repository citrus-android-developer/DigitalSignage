package com.citrus.digitalsignage.di


import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.citrus.digitalsignage.R
import com.citrus.digitalsignage.model.api.ApiService
import com.citrus.digitalsignage.util.Constants
import com.citrus.digitalsignage.util.JsonOrXmlConverterFactory
import com.citrus.digitalsignage.view.MainActivity
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    private const val DEFAULT_CONNECT_TIME = 10L
    private const val DEFAULT_WRITE_TIME = 30L
    private const val DEFAULT_READ_TIME = 30L

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient:OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
            .addConverterFactory(JsonOrXmlConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
            retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ) = context



    @Provides
    @Singleton
    fun provideMainActivityPendingIntent(
        @ApplicationContext app: Context
    ): PendingIntent = PendingIntent.getActivity(
        app,
        0,
        Intent(app, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    @Provides
    @Singleton
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_baseline_reset_tv_24)
        .setContentTitle("TV")
        .setContentText("Restart")
        .setContentIntent(pendingIntent)



}