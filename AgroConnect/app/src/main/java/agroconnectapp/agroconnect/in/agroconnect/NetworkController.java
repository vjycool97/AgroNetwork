package agroconnectapp.agroconnect.in.agroconnect;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.bus.BusProvider;
import agroconnectapp.agroconnect.in.agroconnect.bus.CityUpdateEvent;
import agroconnectapp.agroconnect.in.agroconnect.bus.CommodityUpdateEvent;
import agroconnectapp.agroconnect.in.agroconnect.bus.NetworkErrorEvent;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.CPDataNode;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.CPNode;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.City;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.Commodity;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.User;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.network.TRequestDelegate;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.network.TService;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.CPCropsResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.CitiesResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.CommodityResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.ErrorResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.OKResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.TResponse;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;


public class NetworkController {
    // Instance
    private static NetworkController ourIntance = null;
    public static NetworkController getInstance() {
        if(ourIntance == null) {
            ourIntance = new NetworkController();
        }
        return ourIntance;
    }


    String token = null;
    String selectedLocale = "en";
    int mCurrentCommodityVer = 0;
    int mCurrentCityVer = 0;


    private String lat="", lng="";

    public User getmUser() {
        return mUser;
    }

    public User mUser = null;

    public List<City> mCityList = new ArrayList<>();
    public List<Commodity> mCommodityList = new ArrayList<>();


    public HashMap<String, City> idVsCityHashMap  = new HashMap<>();
    public HashMap<String, Commodity> idVsCommodityHashMap  = new HashMap<>();


    NetworkController() {
    }

    public City getCity(String id) {
        return idVsCityHashMap.get(id);
    }

    public Commodity getCommodity(String id) {
        return idVsCommodityHashMap.get(id);
    }

    public void sortCityAndCommodity() {
        Collections.sort(mCityList, new Comparator<City>() {
            @Override
            public int compare(City c1, City c2) {
                return c1.getLocalName().compareTo(c2.getLocalName());
            }
        });

        Collections.sort(mCommodityList, new Comparator<Commodity>() {
            @Override
            public int compare(Commodity c1, Commodity c2) {
                return c1.getLocalName().compareTo(c2.getLocalName());
            }
        });
    }

    public boolean loadFromDB() {
        mCurrentCityVer = SharedPrefHandler.retreiveInt(Constants.cityVersion, 0);
        mCurrentCommodityVer = SharedPrefHandler.retreiveInt(Constants.commodityVersion, 0);

        mCityList.clear();
        mCommodityList.clear();
        idVsCityHashMap.clear();
        idVsCommodityHashMap.clear();

        List<City> cityList = City.listAll(City.class);
        for(City city : cityList) {
            mCityList.add(city);
            idVsCityHashMap.put(city.getCityId(), city);
        }

        List<Commodity> commodityList = Commodity.listAll(Commodity.class);
        for(Commodity commodity : commodityList) {
            mCommodityList.add(commodity);
            idVsCommodityHashMap.put(commodity.getCommodityId(), commodity);
        }

        return (mCityList.size() > 0 && mCommodityList.size() > 0);
    }

    SaveTODBAsyncTask saveTODBAsyncTask = null;
    private void saveToDbAsync() {
        if(saveTODBAsyncTask == null) {
            saveTODBAsyncTask = new SaveTODBAsyncTask(mCityList, mCommodityList, mUpdateCities, mUpdateCommodities, mCurrentCityVer, mCurrentCommodityVer);
            saveTODBAsyncTask.execute((Object[]) null);
        } else {
            saveTODBAsyncTask.cancel(true);
        }
    }

    class SaveTODBAsyncTask extends AsyncTask {
        List<City> cityList = null;
        List<Commodity> commodityList = null;
        boolean updateCities = false;
        boolean updateCommodities =false;
        int cityVersion;
        int commodityVersion;

        SaveTODBAsyncTask(List<City> cities, List<Commodity> commodities, boolean _updateCities, boolean _updateCommodities, int cityVer, int commodityVer) {
            cityList = new ArrayList<>(cities);
            commodityList = new ArrayList<>(commodities);
            updateCities = _updateCities;
            updateCommodities = _updateCommodities;
            cityVersion = cityVer;
            commodityVersion = commodityVer;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if(updateCities && cityList != null) {
                updateCities = false;
                City.deleteAll(City.class);

                if(isCancelled()) return null;

                SharedPrefHandler.saveInt(Constants.cityVersion, cityVersion);
                for (City city : cityList) {
                    if(isCancelled()) return null;
                    city.save();
                }
            }

            if(updateCommodities && commodityList != null) {
                updateCommodities = false;
                Commodity.deleteAll(Commodity.class);
                if(isCancelled()) return null;
                SharedPrefHandler.saveInt(Constants.commodityVersion, commodityVersion);
                for (Commodity commodity : commodityList) {
                    if(isCancelled()) return null;
                    commodity.save();
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            saveTODBAsyncTask = null;
            saveToDbAsync();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            saveTODBAsyncTask = null;
        }
    }

    //public HashMap<Integer, StoreMenuItem> idVsStoreMenuItemMap  = new HashMap<>();

    public void init(boolean refetchCityAndCommodity) {
        token = SharedPrefHandler.retreiveString(Constants.token, "");
        selectedLocale = SharedPrefHandler.retreiveString(Constants.selectedLocale, null);

        /*if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(MyApp.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MyApp.applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Timber.e("Location Permission denied");
        } else {
            LocationManager locationManager = (LocationManager) MyApp.applicationContext.getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (location != null) {
                try {
                    lat = String.valueOf(location.getLatitude());
                    lng = String.valueOf(location.getLongitude());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }*/

        lat = SharedPrefHandler.retreiveString(Constants.locationLat, "");
        lng = SharedPrefHandler.retreiveString(Constants.locationLong, "");

        if(lat.isEmpty() || lng.isEmpty()) {

        }




        if(refetchCityAndCommodity) {
            mCurrentCommodityVer = 0;
            mCurrentCityVer = 0;
        }

        // Why do we have this check here
        if (null != selectedLocale) {
            populateCities();
        }

    }


    /*public void poplateUser(String number) {
        TService.getInstance().getUser(number, new TRequestDelegate() {
            @Override
            public void run(TResponse response) {
                if(response instanceof UserResponse) {
                    UserResponse res = (UserResponse) response;
                    mUser = res.getUser();
                    *//*
                    *  Saving user data to preference...
                    *//*
                    PrefDataHandler.getInstance().getEditor().putInt(KeyIDS.AGENT_ID, mUser.getId()).apply();
                    PrefDataHandler.getInstance().getEditor().putString(KeyIDS.NAME, mUser.getName()).apply();
                    PrefDataHandler.getInstance().getEditor().putString(KeyIDS.CITY, mUser.getCity()).apply();
                    PrefDataHandler.getInstance().getEditor().putInt(KeyIDS.CITYID, mUser.getCityId()).apply();
                    PrefDataHandler.getInstance().getEditor().putString(KeyIDS.COMMODITY, mUser.getCommodity()).apply();
                    PrefDataHandler.getInstance().getEditor().putInt(KeyIDS.COMMODITYID, mUser.getCommodityId()).apply();
                    PrefDataHandler.getInstance().getEditor().putInt(KeyIDS.AGENT_TYPE, mUser.getAgentType()).apply();
                    if (mUser.getOrganisation() != null)
                    PrefDataHandler.getInstance().getEditor().putString(KeyIDS.ORGANIZATION, mUser.getOrganisation().toString()).apply();
                    if (mUser.getDepartment() != null)
                    PrefDataHandler.getInstance().getEditor().putString(KeyIDS.DEPARTMENT, mUser.getDepartment().toString()).apply();
                    if (mUser.getEmail() != null)
                    PrefDataHandler.getInstance().getEditor().putString(KeyIDS.EMAIL, mUser.getEmail().toString()).apply();
                } else if(response instanceof ErrorResponse){
                    ErrorResponse res = (ErrorResponse) response;
                    Timber.d("poplateUser " + res.getErrorMessage());
                }
            }
        });
    }*/

    boolean mUpdateCities = false;
    private void populateCities() {
        TService.getInstance().getAllCities(selectedLocale, token, mCurrentCityVer, lat, lng, new TRequestDelegate() {
            @Override
            public void run(TResponse response) {
                if(response instanceof CitiesResponse) {
                    CitiesResponse res = (CitiesResponse) response;
                    mUpdateCities = res.getCityData().getVersion() > mCurrentCityVer && res.getCityData().cities.size() > 0;
                    if(mUpdateCities) {
                        mCityList.clear();
                        idVsCityHashMap.clear();
                        mCurrentCityVer = res.getCityData().getVersion();
                        for (City city : res.getCityData().cities) {
                            mCityList.add(city);
                            idVsCityHashMap.put(city.getCityId(), city);
                        }
                    }

                    BusProvider.getInstance().post(new CityUpdateEvent(true));
                    populateCommodities();
                }
                else if(response instanceof ErrorResponse) {
                    ErrorResponse errorResponse = (ErrorResponse) response;
                    BusProvider.getInstance().post(new NetworkErrorEvent(errorResponse.getErrorMessage()));
                    Toast.makeText(AgroConnect.getInstance().getContext(), errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    boolean mUpdateCommodities = false;
    private void populateCommodities() {
        TService.getInstance().getAllCommodities(selectedLocale, token, mCurrentCommodityVer, lat, lng, new TRequestDelegate() {
            @Override
            public void run(TResponse response) {
                if(response instanceof CommodityResponse) {
                    CommodityResponse res = (CommodityResponse) response;
                    mUpdateCommodities = res.getCommodityData().getVersion() > mCurrentCommodityVer && res.getCommodityData().commodities.size() > 0;
                    if(mUpdateCommodities) {
                        mCommodityList.clear();
                        idVsCommodityHashMap.clear();
                        mCurrentCommodityVer = res.getCommodityData().getVersion();
                        for (Commodity commodity : res.getCommodityData().commodities) {
                            mCommodityList.add(commodity);
                            idVsCommodityHashMap.put(commodity.getCommodityId(), commodity);
                        }
                    }

                    BusProvider.getInstance().post(new CommodityUpdateEvent(true));
                    saveToDbAsync();
                }
                else if(response instanceof ErrorResponse) {
                    ErrorResponse errorResponse = (ErrorResponse) response;
                    BusProvider.getInstance().post(new NetworkErrorEvent(errorResponse.getErrorMessage()));
                    Toast.makeText(AgroConnect.getInstance().getContext(), errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    public List<CPNode> mCPNodes = new ArrayList<>();
    public HashMap<Integer, CPNode> idVsCPNodeHashMap  = new HashMap<>();
    public HashMap<Integer, CPDataNode> cropDdVsCPNodeDataHashMap  = new HashMap<>();

    public void loadCPDataFromDB() {
        mCPNodes.clear();
        idVsCPNodeHashMap.clear();

        List<CPNode> cpNodeList = Commodity.listAll(CPNode.class);
        for(CPNode cpNode : cpNodeList) {
            mCPNodes.add(cpNode);
            idVsCPNodeHashMap.put(cpNode.getNodeId(), cpNode);
        }

        List<CPDataNode> cpDataNodes = CPDataNode.listAll(CPDataNode.class);
        for(CPDataNode cpDataNode : cpDataNodes) {
            cropDdVsCPNodeDataHashMap.put(cpDataNode.getParentCropId(), cpDataNode);
        }
    }

    public void populateCPCrops(final TRequestDelegate requestDelegate) {
        // Offline mode
        if (!AgroConnect.isConnected() && NetworkController.getInstance().mCPNodes.size() != 0) {
            requestDelegate.run(new OKResponse());
            if(BuildConfig.DEBUG)
                Toast.makeText(AgroConnect.getInstance().getContext(), "Offline mode", Toast.LENGTH_LONG).show();
            return;
        }

        TService.getInstance().getCPCrops(new TRequestDelegate() {
            @Override
            public void run(TResponse response) {
                if (response instanceof CPCropsResponse) {
                    CPCropsResponse res = (CPCropsResponse) response;

                    //Cache Crops
                    CPNode.deleteAll(CPNode.class);
                    for (CPNode cpNode : res.getCpNodes()) {
                        cpNode.save();
                    }

                    loadCPDataFromDB();

                    requestDelegate.run(new OKResponse());
                } else if (response instanceof ErrorResponse) {
                    ErrorResponse errorResponse = (ErrorResponse) response;
                    //BusProvider.getInstance().post(new NetworkErrorEvent(errorResponse.getErrorMessage()));
                    Toast.makeText(AgroConnect.getInstance().getContext(), errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    requestDelegate.run(errorResponse);
                }
            }
        });
    }

    public void populateCPDataForCrop(final CPNode crop, final TRequestDelegate requestDelegate) {
        final CPDataNode cpDataNode = cropDdVsCPNodeDataHashMap.get(crop.getNodeId());
        // If data for this crop already exists and its latest version
        if(cpDataNode != null && cpDataNode.getVersionNumber() >= crop.getJsonVersion()) {
            try {
                JSONArray jsonArray = new JSONArray(cpDataNode.getJasonArray());
                crop.setChidlen(jsonArray);
                buildTree(crop);
                requestDelegate.run(new OKResponse());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }


        TService.getInstance().getCPCropData(crop, new TRequestDelegate() {
            @Override
            public void run(TResponse response) {
                if(response instanceof OKResponse) {
                    try {
                        // Delete old CPData if present and Save CPData for this crop
                        if(cpDataNode != null) {
                            cropDdVsCPNodeDataHashMap.remove(crop.getNodeId());
                            cpDataNode.delete();
                        }
                        JSONArray jsonArray = crop.getChidlen();
                        CPDataNode _cpDataNode = new CPDataNode(crop.getNodeId(), jsonArray.toString(), crop.getJsonVersion());
                        _cpDataNode.save();
                        cropDdVsCPNodeDataHashMap.put(crop.getNodeId(), _cpDataNode);

                        buildTree(crop);
                        requestDelegate.run(new OKResponse());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if(response instanceof ErrorResponse) {
                    ErrorResponse errorResponse = (ErrorResponse) response;
                    //BusProvider.getInstance().post(new NetworkErrorEvent(errorResponse.getErrorMessage()));
                    requestDelegate.run(errorResponse);

                }

            }
        });
    }

    private void buildTree(CPNode crop) throws JSONException {
        JSONArray jsonArray = crop.getChidlen();
        if (jsonArray == null) {
            crop.setNode_type(CPNode.NODE_TYPE.LEAF);
            return;
        }

        List list = new ArrayList<CPNode>();
        for (int count = 0; count < jsonArray.length(); count++) {
            JSONObject obj = jsonArray.optJSONObject(count);
            CPNode node = new CPNode();
            if (obj.has("Children")) { // Branch
                node.setData(obj);
                list.add(node);
                node.setNode_type(CPNode.NODE_TYPE.BRANCH);
                buildTree(node);
            } else {                  // Leaf
                node.leaf = obj;
                node.setNode_type(CPNode.NODE_TYPE.LEAF);
                crop.setNode_type(CPNode.NODE_TYPE.LEAF);
                list.add(node);
            }
        }
        crop.setChildrenCropList(list);
    }


}
