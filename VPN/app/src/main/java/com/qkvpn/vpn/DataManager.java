package com.qkvpn.vpn;





public class DataManager {



    public static final String Main_Api = "https://darkvpnpro.raufcoders.com/";


    public static final String settings = Main_Api+"api.php?action=get_settings";
    public static final String servers = Main_Api + "api.php?action=get_all_servers";
    public static final String feedback = Main_Api+"user.php";
    public static boolean ADMOB_ENABLE = true;

    public static final String ONE_SUBSCRIPTION_ID = "one_month_subscription";
    public static final String THREE_SUBSCRIPTION_ID = "three_month_subscription";
    public static final String YEARLY_SUBSCRIPTION_ID = "yearly_month_subscription";
    public static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkPVNFaPm1piP4EgSPJM6JepuhxIqzlRq9IvcCEioXskpK6X7dk+9hQskT6RComON/X9OJTxCUURNWS+gbz428QnNx/F5RY8Sqm5VHbX2guYVopbyvb0BZ0sqb2aiRs/x1Qnk7fqk8EDS1JazhPVx/nweBMZXahb/FmZzF95VBYhWVLvPKH8LKi640rT/HLwQzkUX0ZBQLDdnlvJC1wjXzfFL/gAemGYjwLaC37JycjCdAhpNzS76MgwUCPm43PJOsQZgsXjA5Zvw2QnnISxpzj6dMO11o777yBlN7zI9i0q4SvoIEi3obu4nwOjaZr9rhIXgZguqBZaTMY/LGpofTQIDAQAB"; // PUT YOUR MERCHANT KEY HERE;
    public static final String MERCHANT_ID="11356167460528445992";


    public static Boolean readyToPurchase =false;
}
