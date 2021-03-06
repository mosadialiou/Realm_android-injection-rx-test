package tank.viraj.realm.injections;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tank.viraj.realm.adapter.MainAdapter;
import tank.viraj.realm.dao.GitHubUserDao;
import tank.viraj.realm.dao.GitHubUserProfileDao;
import tank.viraj.realm.dataSource.GitHubUserListDataSource;
import tank.viraj.realm.dataSource.GitHubUserProfileDataSource;
import tank.viraj.realm.presenter.GitHubUserPresenter;
import tank.viraj.realm.presenter.GitHubUserProfilePresenter;
import tank.viraj.realm.retrofit.GitHubApiInterface;
import tank.viraj.realm.util.InternetConnection;
import tank.viraj.realm.util.RxSchedulerConfiguration;

/**
 * Created by Viraj Tank, 18-06-2016.
 */

@Module
public class ApplicationModule {
    private static final String baseUrl = "https://api.github.com/";

    private Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    /* DAO (data access object) for GitHubUser model */
    @Provides
    GitHubUserDao provideGitHubUserDao() {
        return new GitHubUserDao();
    }

    /* DAO (data access object) for GitHubUser model */
    @Provides
    GitHubUserProfileDao provideGitHubUserProfileDao() {
        return new GitHubUserProfileDao();
    }

    /* Presenter for GitHubUser */
    @Provides
    GitHubUserPresenter provideGitHubPresenter(GitHubUserListDataSource gitHubUserListDataSource,
                                               RxSchedulerConfiguration rxSchedulerConfiguration,
                                               InternetConnection internetConnection) {
        return new GitHubUserPresenter(gitHubUserListDataSource, rxSchedulerConfiguration, internetConnection);
    }

    /* Presenter for GitHubUserProfile */
    @Provides
    GitHubUserProfilePresenter provideProfilePresenter(
            GitHubUserProfileDataSource gitHubUserProfileDataSource,
            RxSchedulerConfiguration rxSchedulerConfiguration,
            InternetConnection internetConnection) {
        return new GitHubUserProfilePresenter(gitHubUserProfileDataSource, rxSchedulerConfiguration, internetConnection);
    }

    /* Data source for GitHubUserListDataSource */
    @Provides
    GitHubUserListDataSource provideGitHubUserListDataSource(GitHubApiInterface gitHubApiInterface,
                                                             GitHubUserDao gitHubUserDao,
                                                             InternetConnection internetConnection,
                                                             RxSchedulerConfiguration rxSchedulerConfiguration) {
        return new GitHubUserListDataSource(
                gitHubApiInterface, gitHubUserDao, internetConnection, rxSchedulerConfiguration);
    }

    /* Data source for GitHubUserProfileDataSource */
    @Provides
    GitHubUserProfileDataSource provideGitHubUserProfileDataSource(
            GitHubApiInterface gitHubApiInterface, GitHubUserProfileDao gitHubUserProfileDao,
            InternetConnection internetConnection, RxSchedulerConfiguration rxSchedulerConfiguration) {
        return new GitHubUserProfileDataSource(
                gitHubApiInterface, gitHubUserProfileDao, internetConnection, rxSchedulerConfiguration);
    }

    /* OkHttpclient for retrofit2 */
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    /* retrofit2 */
    @Provides
    @Singleton
    GitHubApiInterface provideGitHubApiInterface(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(GitHubApiInterface.class);
    }

    /* Data adapter for recycler view */
    @Provides
    MainAdapter provideMainAdapter() {
        return new MainAdapter(application.getApplicationContext());
    }

    /* RxSchedulerConfiguration */
    @Provides
    @Singleton
    RxSchedulerConfiguration provideRxSchedulerConfiguration() {
        return new RxSchedulerConfiguration();
    }

    /* InternetConnection utility */
    @Provides
    @Singleton
    InternetConnection provideInternetConnection() {
        return new InternetConnection(application.getApplicationContext());
    }
}