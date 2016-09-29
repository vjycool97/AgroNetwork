package agroconnectapp.agroconnect.in.agroconnect.networkclient.model;

import com.orm.SugarRecord;

/**
 * Created by shakti on 4/30/2016.
 */
public class CPDataNode extends SugarRecord {
    int parentCropId;

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    int versionNumber;
    String jasonArray;

    public CPDataNode() {
    }

    public CPDataNode(int parentCropId, String jasonArray, int versionNumber) {
        this.parentCropId = parentCropId;
        this.jasonArray = jasonArray;
        this.versionNumber = versionNumber;
    }

    public int getParentCropId() {
        return parentCropId;
    }



    public String getJasonArray() {
        return jasonArray;
    }

    public void setJasonArray(String jasonArray) {
        this.jasonArray = jasonArray;
    }

}
