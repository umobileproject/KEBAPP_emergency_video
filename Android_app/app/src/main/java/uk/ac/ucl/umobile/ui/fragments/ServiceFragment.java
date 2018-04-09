
package uk.ac.ucl.umobile.ui.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.intel.jndn.management.types.ForwarderStatus;

import net.grandcentrix.tray.AppPreferences;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import uk.ac.ucl.umobile.App;
import uk.ac.ucl.umobile.data.StatsHandler;
import uk.ac.ucl.umobile.utils.MyNfdc;
import uk.ac.ucl.umobile.utils.G;

import uk.ac.ucl.umobile.R;

public class ServiceFragment extends Fragment {

  public static ServiceFragment newInstance() {
    // Create fragment arguments here (if necessary)
    return new ServiceFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    m_handler = new Handler();
    m_handler.postDelayed(m_statusUpdateRunnable, 1000);
    m_handler.postDelayed(m_ubicdnStatsUpdateRunnable, 1000);


  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState)
  {
    @SuppressLint("InflateParams")
    View v =  inflater.inflate(R.layout.fragment_service, null);


    /* sisSource = (CheckBox) v.findViewById(R.id.checkbox_source);
    isSource.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        App app = (App)getActivity().getApplication();

        if (((CheckBox) v).isChecked()) {
          m_appPreferences.put(PREF_UBICDN_SERVICE_SOURCE,true);
          app.setSource(true);
          G.Log("Set source "+m_appPreferences.getBoolean(PREF_UBICDN_SERVICE_SOURCE,false));
        } else {
          app.setSource(false);
          m_appPreferences.put(PREF_UBICDN_SERVICE_SOURCE,false);
          G.Log("Set source "+m_appPreferences.getBoolean(PREF_UBICDN_SERVICE_SOURCE,false));

        }
      }
    });*/
    m_statusView = (ViewGroup)v.findViewById(R.id.status_view);
    //m_statusView.setVisibility(View.GONE);
    m_versionView = (TextView)v.findViewById(R.id.version);
    m_uptimeView = (TextView)v.findViewById(R.id.uptime);
    m_nameTreeEntriesView = (TextView)v.findViewById(R.id.name_tree_entries);
    m_fibEntriesView = (TextView)v.findViewById(R.id.fib_entries);
    m_pitEntriesView = (TextView)v.findViewById(R.id.pit_entries);
    m_measurementEntriesView = (TextView)v.findViewById(R.id.measurement_entries);
    m_csEntriesView = (TextView)v.findViewById(R.id.cs_entries);
    m_inInterestsView = (TextView)v.findViewById(R.id.in_interests);
    m_outInterestsView = (TextView)v.findViewById(R.id.out_interests);
    m_inDataView = (TextView)v.findViewById(R.id.in_data);
    m_outDataView = (TextView)v.findViewById(R.id.out_data);
    m_inNacksView = (TextView)v.findViewById(R.id.in_nacks);
    m_outNacksView = (TextView)v.findViewById(R.id.out_nacks);


    m_wifi_status_view = (ViewGroup)v.findViewById(R.id.wifi_status_view);
   // m_wifi_status_view.setVisibility(View.GONE);

    m_discoveries = (TextView) v.findViewById(R.id.discoveries);
    m_linkStatus = (TextView) v.findViewById(R.id.link);
    m_Status = (TextView)v.findViewById(R.id.status);
    m_hsSSID = (TextView)v.findViewById(R.id.sd);
    m_connections = (TextView)v.findViewById(R.id.ap);
    m_hsClients = (TextView)v.findViewById(R.id.restarts);

    m_btStatusView = (ViewGroup)v.findViewById(R.id.bt_status_view);
   // m_btStatusView.setVisibility(View.GONE);
    m_btCtView = (TextView)v.findViewById(R.id.btconnect);
    m_btStView = (TextView)v.findViewById(R.id.btstatus);
    return v;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState)
  {
      G.Log("ServiceFragment::onActivityCreated()");
      super.onActivityCreated(savedInstanceState);
      m_sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
      m_appPreferences = new AppPreferences(getContext()); // this Preference comes for free from the library

  }

  @Override
  public void
  onResume() {
    G.Log("ServiceFragment::onResume()");
    super.onResume();
      //boolean shouldBeSource = m_sharedPreferences.getBoolean(PREF_UBICDN_SERVICE_SOURCE, false);
      //G.Log("Shared preferences "+m_sharedPreferences.getBoolean(PREF_UBICDN_SERVICE_SOURCE,false));
      boolean shouldBeSource = m_appPreferences.getBoolean(PREF_UBICDN_SERVICE_SOURCE,false);
      //isSource.setChecked(shouldBeSource);
  }

  @Override
  public void
  onPause() {
    super.onPause();
    G.Log("ServiceFragment::onPause()");

    m_handler.removeCallbacks(m_statusUpdateRunnable);
    m_handler.removeCallbacks(m_ubicdnStatsUpdateRunnable);
    //m_handler.removeCallbacks(m_retryConnectionToService);
  }


  private class StatusUpdateTask extends AsyncTask<Void, Void, ForwarderStatus> {
    /**
     * @param voids
     * @return ForwarderStatus if operation succeeded, null if operation failed
     */
    @Override
    protected ForwarderStatus
    doInBackground(Void... voids)
    {
      try {
        MyNfdc nfdcHelper = new MyNfdc();
        ForwarderStatus fs = nfdcHelper.generalStatus();
        nfdcHelper.shutdown();
        return fs;
      }
      catch (Exception e) {
        G.Log("Servicefragment","Error communicating with NFD (" + e.getMessage() + ")");
        return null;
      }
    }

    @Override
    protected void
    onPostExecute(ForwarderStatus fs)
    {
      if (fs == null) {
        // when failed, try after 0.5 seconds
        m_handler.postDelayed(m_statusUpdateRunnable, 500);
      }
      else {
        m_versionView.setText(fs.getNfdVersion());
        m_uptimeView.setText(PeriodFormat.getDefault().print(new Period(
          fs.getCurrentTimestamp() - fs.getStartTimestamp())));
        m_nameTreeEntriesView.setText(String.valueOf(
          fs.getNNameTreeEntries()));
        m_fibEntriesView.setText(String.valueOf(fs.getNFibEntries()));
        m_pitEntriesView.setText(String.valueOf(fs.getNPitEntries()));
        m_measurementEntriesView.setText(String.valueOf(
          fs.getNMeasurementsEntries()));
        m_csEntriesView.setText(String.valueOf(fs.getNCsEntries()));

        m_inInterestsView.setText(String.valueOf(fs.getNInInterests()));
        m_outInterestsView.setText(String.valueOf(fs.getNOutInterests()));

        m_inDataView.setText(String.valueOf(fs.getNInDatas()));
        m_outDataView.setText(String.valueOf(fs.getNOutDatas()));

        m_inNacksView.setText(String.valueOf(fs.getNInNacks()));
        m_outNacksView.setText(String.valueOf(fs.getNOutNacks()));


          // refresh after 5 seconds
        m_handler.postDelayed(m_statusUpdateRunnable, 5000);
      }
    }
  }


  private class UbiCDNUpdateTask extends AsyncTask<Void, Void, StatsHandler> {
    /**
     * @param voids
     * @return ForwarderStatus if operation succeeded, null if operation failed
     */
    @Override
    protected StatsHandler
    doInBackground(Void... voids)
    {
      try {
        //App app = (App)getActivity().getApplication();
        StatsHandler st = new StatsHandler(getContext());
        if(!st.getBtStatus().equals(""))
          return st;
        else
          return null;
      }
      catch (Exception e) {
        G.Log("Servicefragment","Error communicating with NFD (" + e.getMessage() + ")");
        return null;
      }

    }

    @Override
    protected void
    onPostExecute(StatsHandler fs)
    {
      if (fs == null) {
        // when failed, try after 0.5 seconds
        m_handler.postDelayed(m_ubicdnStatsUpdateRunnable, 500);
      }
      else {

        m_btStatusView.setVisibility(View.VISIBLE);
        m_btStView.setText(fs.getBtStatus());
        m_btCtView.setText(String.valueOf(fs.getBtConnections()));
        m_hsSSID.setText(fs.getHsSSID());
        m_connections.setText(String.valueOf(fs.getConnections()));
        m_discoveries.setText(String.valueOf(fs.getDiscoveries()));
        m_Status.setText(fs.getDiscoveryStatus());
        m_hsClients.setText(String.valueOf(fs.getHsClients()));
        m_linkStatus.setText(fs.getLinkStatus());
        //m_wifi_status_view.setVisibility(View.VISIBLE);

        // refresh after 5 seconds
        m_handler.postDelayed(m_ubicdnStatsUpdateRunnable, 5000);
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  //private CheckBox isSource;

  /** ListView holding UbiCDN  status information */
  private ViewGroup m_statusView;

  private TextView m_versionView;
  private TextView m_uptimeView;
  private TextView m_nameTreeEntriesView;
  private TextView m_fibEntriesView;
  private TextView m_pitEntriesView;
  private TextView m_measurementEntriesView;
  private TextView m_csEntriesView;
  private TextView m_inInterestsView;
  private TextView m_outInterestsView;
  private TextView m_inDataView;
  private TextView m_outDataView;
  private TextView m_inNacksView;
  private TextView m_outNacksView;


  private ViewGroup m_btStatusView;
  private TextView m_btStView;
  private TextView m_btCtView;

  private ViewGroup m_wifi_status_view;


  private TextView m_hsSSID;
  private TextView m_connections;
  private TextView m_discoveries;

  private TextView m_Status;
  private TextView m_hsClients;
  private TextView m_linkStatus;

  private Handler m_handler;
  private Runnable m_statusUpdateRunnable = new Runnable() {
    @Override
    public void run()
    {
      new StatusUpdateTask().execute();
    }
  };

  private Runnable m_ubicdnStatsUpdateRunnable = new Runnable() {
    @Override
    public void run()
    {
      new UbiCDNUpdateTask().execute();
    }
  };

  private SharedPreferences m_sharedPreferences;
  private AppPreferences m_appPreferences;
//  private Messenger m_serviceMessenger2 = null;

    public static final String PREF_UBICDN_SERVICE_STATUS = "UBICDN_SERVICE_STATUS";
  public static final String PREF_UBICDN_SERVICE_SOURCE = "UBICDN_SERVICE_TYPE";

  private AlertDialog dialogVpn = null;

  private static final int REQUEST_VPN = 1;
  private static final int REQUEST_INVITE = 2;
  private static final int REQUEST_LOGCAT = 3;
  public static final int REQUEST_ROAMING = 4;

    private int restarts=0;

}
