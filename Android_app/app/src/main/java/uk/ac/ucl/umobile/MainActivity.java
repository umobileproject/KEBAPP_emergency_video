package uk.ac.ucl.umobile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.intel.jndn.management.types.FaceStatus;
import com.intel.jndn.management.types.RibEntry;

//import uk.ac.ucl.umobile.backend.VideoListService;
import uk.ac.ucl.umobile.ui.fragments.FaceListFragment;
import uk.ac.ucl.umobile.ui.fragments.FaceStatusFragment;
import uk.ac.ucl.umobile.ui.fragments.LogcatFragment;
import uk.ac.ucl.umobile.ui.fragments.LogcatSettingsFragment;
import uk.ac.ucl.umobile.ui.fragments.RouteInfoFragment;
import uk.ac.ucl.umobile.ui.fragments.RouteListFragment;
import uk.ac.ucl.umobile.ui.fragments.ServiceFragment;
import uk.ac.ucl.umobile.ui.fragments.DrawerFragment;
import uk.ac.ucl.umobile.ui.fragments.SettingsFragment;
import uk.ac.ucl.umobile.ui.fragments.list.kiosk.KioskFragment;
import uk.ac.ucl.umobile.utils.G;

import java.util.ArrayList;

//import static uk.ac.ucl.umobile.backend.NotificationService.NEW_VIDEO;

/**
 * Created by srenevic on 24/08/17.
 *
 * Main activity of the ubicdn app
 *
 */

public class MainActivity extends AppCompatActivity
    implements DrawerFragment.DrawerCallbacks,
               LogcatFragment.Callbacks,
               FaceListFragment.Callbacks,
               RouteListFragment.Callbacks
               //VideoListFragment.Callbacks*/
{

    //////////////////////////////////////////////////////////////////////////////

    /** Reference to drawer fragment */
    private DrawerFragment m_drawerFragment;
    public static final boolean DEBUG = true;


    /** Title that is to be displayed in the ActionBar */
    private int m_actionBarTitleId = -1;

    /** Item code for drawer items: For use in onDrawerItemSelected() callback */
    public static final int DRAWER_ITEM_GENERAL = 1;
    public static final int DRAWER_ITEM_NFD = 2;
    public static final int DRAWER_ITEM_FACES = 3;
    public static final int DRAWER_ITEM_ROUTES = 4;
    // public static final int DRAWER_ITEM_STRATEGIES = 4;
    public static final int DRAWER_ITEM_LOGCAT = 5;
    public static final int DRAWER_ITEM_SETTINGS = 6;

    private ProgressDialog mProgressDialog;
    private static final String TAG = "MainActivity";
   // private FirebaseAuth mAuth;


    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int  PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 2;

    @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


   // Intent intent = new Intent(this, VideoListService.class);
   // startService(intent);

   // FirebaseMessaging.getInstance().subscribeToTopic("news");

    Log.d("Main", "subscribed to topic news");
    FragmentManager fragmentManager = getSupportFragmentManager();
    //mAuth = FirebaseAuth.getInstance();


       Toolbar toolbar = findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //  builder.setTitle("This app needs location access");
            //  builder.setMessage("Please grant location access");
            //  builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                }
            });
            builder.show();
        }
    }


    if (savedInstanceState != null) {
      m_drawerFragment = (DrawerFragment)fragmentManager.findFragmentByTag(DrawerFragment.class.toString());
    }

    if (m_drawerFragment == null) {
      ArrayList<DrawerFragment.DrawerItem> items = new ArrayList<DrawerFragment.DrawerItem>();
      items.add(new DrawerFragment.DrawerItem(R.string.drawer_item_general, 0,
                                             DRAWER_ITEM_GENERAL));
      items.add(new DrawerFragment.DrawerItem(R.string.drawer_item_service, 0,
                                             DRAWER_ITEM_NFD));
      items.add(new DrawerFragment.DrawerItem(R.string.drawer_item_settings, 0,
                                             DRAWER_ITEM_SETTINGS));
      items.add(new DrawerFragment.DrawerItem(R.string.drawer_item_logcat, 0,
                                             DRAWER_ITEM_LOGCAT));
      items.add(new DrawerFragment.DrawerItem(R.string.drawer_item_faces, 0,
                                              DRAWER_ITEM_FACES));
      items.add(new DrawerFragment.DrawerItem(R.string.drawer_item_routes, 0,
                                              DRAWER_ITEM_ROUTES));
      //    items.add(new DrawerFragment.DrawerItem(R.string.drawer_item_strategies, 0,
      //                                            DRAWER_ITEM_STRATEGIES));


      m_drawerFragment = DrawerFragment.newInstance(items);

      fragmentManager
        .beginTransaction()
        .replace(R.id.navigation_drawer, m_drawerFragment, DrawerFragment.class.toString())
        .commit();

    }

  }


  @Override
  public void onStart() {
    super.onStart();
    //signInAnonymously();
    G.Log(TAG,"onstart");
     /* if (getIntent().getExtras() != null) {
          for (String key : getIntent().getExtras().keySet()) {
              Object value = getIntent().getExtras().get(key);
              Log.d(TAG, "Key: " + key + " Value: " + value);
          }
          String video = getIntent().getStringExtra("title");
          String desc = getIntent().getStringExtra("desc");
          String url = getIntent().getStringExtra("url");
          String action = NEW_VIDEO;

          Intent broadcast = new Intent(action)
                  .putExtra("title", video)
                  .putExtra("desc", desc)
                  .putExtra("url", url);
          sendBroadcast(broadcast);
      }*/

  }



  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.d(TAG,"onCreateOptionsMenu" + String.valueOf(m_drawerFragment.shouldHideOptionsMenu()));
    if (!m_drawerFragment.shouldHideOptionsMenu()) {
      updateActionBar();
      return super.onCreateOptionsMenu(menu);
    }
    else
      return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
   * Convenience method that updates and display the current title in the Action Bar
   */
  @SuppressWarnings("deprecation")
  private void updateActionBar() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setDisplayShowTitleEnabled(true);
    if (m_actionBarTitleId != -1) {
      actionBar.setTitle(m_actionBarTitleId);
    }
  }

  /**
   * Convenience method that replaces the main fragment container with the
   * new fragment and adding the current transaction to the backstack.
   *
   * @param fragment Fragment to be displayed in the main fragment container.
   */
  private void replaceContentFragmentWithBackstack(Fragment fragment) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction()
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .replace(R.id.main_fragment_container, fragment)
        .addToBackStack(null)
        .commit();
  }

  //////////////////////////////////////////////////////////////////////////////

  @Override
  public void
  onDrawerItemSelected(int itemCode, int itemNameId) {

    String fragmentTag = "org.schabi.newpipe.content-" + String.valueOf(itemCode);
    FragmentManager fragmentManager = getSupportFragmentManager();

    // Create fragment according to user's selection
    Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
    if (fragment == null) {
      switch (itemCode) {
        case DRAWER_ITEM_GENERAL:
        //  fragment = VideoListFragment.newInstance();
       try {
            fragment = KioskFragment.getInstance(0,"Trending");

      } catch (Exception e) {}
          break;
        case DRAWER_ITEM_NFD:
            fragment = ServiceFragment.newInstance();
          break;
        case DRAWER_ITEM_FACES:
            fragment = FaceListFragment.newInstance();
          break;
        case DRAWER_ITEM_ROUTES:
            fragment = RouteListFragment.newInstance();
          break;
        // TODO: Placeholders; Fill these in when their fragments have been created
        //    case DRAWER_ITEM_STRATEGIES:
        //      break;
        case DRAWER_ITEM_LOGCAT:
          fragment = LogcatFragment.newInstance();
          break;
        case DRAWER_ITEM_SETTINGS:
          fragment = SettingsFragment.newInstance();
          break;
        default:
          // Invalid; Nothing else needs to be done
          return;
      }
    }

    // Update ActionBar title
    m_actionBarTitleId = itemNameId;

    fragmentManager.beginTransaction()
      .replace(R.id.main_fragment_container, fragment, fragmentTag)
      .commit();
  }

    private void showProgressDialog(String caption) {

        mProgressDialog = new ProgressDialog(this);

        final String msg = caption;

        new Thread()
        {
            public void run()
            {
                MainActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        if (mProgressDialog == null) {
                            mProgressDialog.setIndeterminate(true);
                        }

                        mProgressDialog.setMessage(msg);
                        mProgressDialog.show();
                    }
                });
            }
        }.start();


    }
  @Override
  public void onDisplayLogcatSettings() {
    replaceContentFragmentWithBackstack(LogcatSettingsFragment.newInstance());
  }

  @Override
  public void onFaceItemSelected(FaceStatus faceStatus) {
    replaceContentFragmentWithBackstack(FaceStatusFragment.newInstance(faceStatus));
  }

  @Override
  public void onRouteItemSelected(RibEntry ribEntry)
  {
    replaceContentFragmentWithBackstack(RouteInfoFragment.newInstance(ribEntry));
  }

  /*@Override
  public void onVideoItemSelected(String videoEntry)
  {

    replaceContentFragmentWithBackstack(VideoFragment.newInstance(videoEntry));
  }*/


    /*private void signInAnonymously() {
    // Sign in anonymously. Authentication is required to read or write from Firebase Storage.
    showProgressDialog(getString(R.string.progress_auth));
    mAuth.signInAnonymously()
            .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
              @Override
              public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "signInAnonymously:SUCCESS");
                hideProgressDialog();
                updateUI(authResult.getUser());
              }
            })
            .addOnFailureListener(this, new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "signInAnonymously:FAILURE", exception);
                hideProgressDialog();
                updateUI(null);
              }
            });
  }*/



   /* private void updateUI(FirebaseUser user) {
        // Signed in or Signed out
        if(user==null) {
            String msg = "Unable to athenticate to notifications server";
            Log.d(TAG, msg);
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void hideProgressDialog() {
        new Thread()
        {
            public void run()
            {
                MainActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                });
            }
        }.start();

    }
    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NEW_VIDEO);

        return filter;
    }*/

}
