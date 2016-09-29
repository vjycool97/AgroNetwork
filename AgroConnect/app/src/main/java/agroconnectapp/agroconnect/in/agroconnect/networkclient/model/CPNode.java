package agroconnectapp.agroconnect.in.agroconnect.networkclient.model;

import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shakti on 5/14/2016.
 */

public class CPNode extends SugarRecord {
    public enum NODE_TYPE {INVALID, ROOT, BRANCH, LEAF};

    private Integer nodeId;
    private String Name;
    private String ImageUrl;

    private transient List<String> ImageUrls;

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }


    public CPNode() {}

    private NODE_TYPE node_type = NODE_TYPE.INVALID;

    public int getJsonVersion() {
        return JsonVersion;
    }

    public void setJsonVersion(int jsonVersion) {
        JsonVersion = jsonVersion;
    }

    private int JsonVersion;

    private transient List<CPNode> childrenCropList = null;
    private transient JSONArray children = null;
    public transient JSONObject leaf = null;


    public NODE_TYPE getNode_type() {
        return node_type;
    }

    public void setNode_type(NODE_TYPE node_tyep) {
        this.node_type = node_tyep;
    }

    public boolean isLeaf() { return node_type == NODE_TYPE.LEAF; }


    public List<CPNode> getChildrenCropList() {
        return childrenCropList;
    }

    public void setChildrenCropList(List<CPNode> childrenCropList) {
        this.childrenCropList = childrenCropList;
    }

    public JSONArray getChidlen() {
        return children;
    }

    public void setChidlen(JSONArray chidlen) {
        children = chidlen;
    }



    /**
     * @return The nodeId
     */
    public Integer getNodeId() {
        return nodeId;
    }

    /**
     * @param Id The nodeId
     */
    public void setNodeId(Integer Id) {
        this.nodeId = Id;
    }

    /**
     * @return The Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @param Name The Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * @return The ImageUrls
     */
    private List<String> getImageUrls() {
        return ImageUrls;
    }

    /**
     * @param ImageUrl The ImageUrls
     */
    public void setImageUrls(List<String> ImageUrl) {
        this.ImageUrls = ImageUrl;
        if(ImageUrl.size() > 0)
            setImageUrl(ImageUrl.get(0));
    }

    public void setData(JSONObject object) {
        setNodeId(object.optInt("Id"));
        setName(object.optString("Name"));
        setJsonVersion(object.optInt("JsonVersion"));

        List<String > urs = new ArrayList<>();

        JSONArray urlArray = object.optJSONArray("ImageUrls");
        if(urlArray == null)
            urlArray = object.optJSONArray("ImageUrl");
        if(urlArray != null) {
            for (int j = 0; j < urlArray.length(); j++)
                urs.add(urlArray.optString(j));
        }
        setImageUrls(urs);


        try {
            if(object.has("Children"))
                setChidlen(object.getJSONArray("Children"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
