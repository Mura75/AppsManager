package com.codebusters.appsmanager;


import android.content.pm.PackageInfo;

import com.codebusters.appsmanager.ui.appsList.AppsListPresenter;
import com.codebusters.appsmanager.ui.appsList.AppsListView;
import com.codebusters.appsmanager.ui.appsList.AppsListView$$State;
import com.codebusters.appsmanager.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public final class AppListPresenterTest {

    @Mock
    AppsListView appsListView;

    @Mock
    AppsListView$$State appsListView$$State;

    private AppsListPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new AppsListPresenter();
        presenter.attachView(appsListView);
        presenter.setViewState(appsListView$$State);
    }


    @Test
    public void details_shouldShowDetailsContainer() {
        presenter.getAppsAsync2(true, Constants.SORT_BY_NAME, Constants.ASCENDING);
    }


    public PackageInfo emptyPackage() {
        return new PackageInfo();
    }


}
