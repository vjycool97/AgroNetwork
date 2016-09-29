package agroconnectapp.agroconnect.in.agroconnect.networkclient.response;

import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.CPNode;

/**
 * Created by shakti on 5/14/2016.
 */
public class CPCropsResponse extends TResponse{
    public CPCropsResponse(List<CPNode> cpNodes) {
        this.cpNodes = cpNodes;
    }

    public List<CPNode> getCpNodes() {
        return cpNodes;
    }

    public void setCpNodes(List<CPNode> cpNodes) {
        this.cpNodes = cpNodes;
    }

    List<CPNode> cpNodes = null;
}
