package com.qkvpn.vpn;


public class api_response_model {

    private int server_id,Type;
    private String HostName;
//    private int Type;
    private String flag , ip_cert;


    public int getServer_id() {

        return server_id;
    }

    public void setServer_id(int server_id) {

        this.server_id = server_id;
    }

    public String getHostName() {

        return HostName;
    }

    public void setHostName(String HostName) {

        this.HostName = HostName;
    }




    public int getType() {

        return Type;
    }

    public void setType(int Type) {

        this.Type = Type;
    }



    public String getFlag() {

        return flag;
    }

    public void setFlag(String flag) {

        this.flag = flag;
    }


    public String getIp_cert() {

        return ip_cert;
    }

    public void setIp_cert(String ip_cert) {

        this.ip_cert = ip_cert;
    }

}
