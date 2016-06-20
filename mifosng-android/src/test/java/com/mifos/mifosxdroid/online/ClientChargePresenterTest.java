package com.mifos.mifosxdroid.online;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.FakeRemoteDataSource;
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeMvpView;
import com.mifos.mifosxdroid.online.clientcharge.ClientChargePresenter;
import com.mifos.mifosxdroid.util.RxSchedulersOverrideRule;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rajan Maurya on 20/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientChargePresenterTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    ClientChargePresenter mClientChargePresenter;

    @Mock
    DataManager mDataManager;

    @Mock
    ClientChargeMvpView mClientChargeMvpView;

    Page<Charges> chargesPage;
    int clientId = 1;
    int offset = 0;
    int limit = 10;

    @Before
    public void setUp() {
        mClientChargePresenter = new ClientChargePresenter(mDataManager);
        mClientChargePresenter.attachView(mClientChargeMvpView);

        chargesPage = FakeRemoteDataSource.getCharges();
    }

    @After
    public void tearDown() {
        mClientChargePresenter.detachView();
    }

    @Test
    public void testLoadCharges() {
        when(mDataManager.getClientCharges(clientId, offset, limit))
                .thenReturn(Observable.just(chargesPage));

        mClientChargePresenter.loadCharges(clientId, offset, limit);

        verify(mClientChargeMvpView).showChargesList(chargesPage);
        verify(mClientChargeMvpView, never()).showFetchingErrorCharges("Failed to Load Charges");
    }

    @Test
    public void testLoadChargesFails() {

        when(mDataManager.getClientCharges(clientId, offset, limit))
                .thenReturn(Observable.<Page<Charges>>error(new RuntimeException()));

        mClientChargePresenter.loadCharges(clientId, offset, limit);

        verify(mClientChargeMvpView).showFetchingErrorCharges("Failed to Load Charges");
        verify(mClientChargeMvpView, never()).showChargesList(chargesPage);
    }
}