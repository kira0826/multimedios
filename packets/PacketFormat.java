package packets;

import java.io.Serializable;

public class PacketFormat implements Serializable{
    
    private String operation; 
    private Serializable [] parameters;

    // Constructor, getters and setters. 
    
    public PacketFormat(String operation, Serializable ... parameters) {
        this.operation = operation;
        this.parameters = parameters;
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public Serializable[] getParameters() {
        return parameters;
    }
    public void setParameters(Serializable[] parameters) {
        this.parameters = parameters;
    } 
    
}
