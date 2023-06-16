

import java.sql.*;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        /* Bu uygulamada nortwind veritabanı kullanıldı.kullanıcı adı ve şifre kullanımı için mysql üzerinde view oluşturuldu.

       create VIEW kullanici_bilgi AS SELECT
        concat(lower(FirstName),".",lower(Lastname)) as 'kullanici_adi',
        concat (lower(lastName),CAST(YEAR(birthdate) as char)) as 'sifre',
        EmployeeID from northwind.`dbo.Employees`
         */

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind","root","a1234");
            Statement st = con.createStatement();
            ResultSet strs;
            String sifre;
            String kullanici_adi;
            boolean joinloginloop=false;
            int denemesansi=0;
            int employeeID=0;
            ResultSet rs;


            while (true){
                System.out.println("Kullanici adi giriniz.");
                kullanici_adi = scan.nextLine().toLowerCase();
                System.out.println("Şifre giriniz.");
                sifre = scan.nextLine();
                PreparedStatement idpw = con.prepareStatement("select employeeID from kullanici_bilgi where kullanici_adi=? and sifre=?");
                idpw.setString(1,kullanici_adi);
                idpw.setString(2,sifre);
                rs = idpw.executeQuery();
                if (rs.next()){
                    employeeID= rs.getInt(1);
                    joinloginloop=true;
                    break;
                }
                else{
                    denemesansi++;
                    System.out.println("Kullanıcı adı veya şifre yanlış.Kalan deneme şansı : "+(3-denemesansi));
                    if(denemesansi==3){
                        break;
                    }
                }
            }

            while (joinloginloop){
                try{
                    System.out.println("Yapmak istediğiniz işlemi seçin : ");
                    System.out.println("1- Kaç tane siparişle ilgilendiği");
                    System.out.println("2- Şirkete ne kadar para kazandırdığı");
                    System.out.println("3- Her üründen kaç tane sattığı");
                    System.out.println("4- En çok ilgilendiği müşterinin adı ve id");
                    System.out.println("5- exit");
                    int secim=scan.nextInt();
                    if(secim==1){
                        strs =st.executeQuery("select count(*) as 'siparis sayisi' from `dbo.orders` where `dbo.orders`.EmployeeID="+employeeID);
                        while (strs.next()){
                            System.out.println("Kaç tane siparişle ilgilendiği : "+strs.getInt(1));
                        }

                    }
                    if(secim==2){
                        strs=st.executeQuery("select sum(Quantity*unitPrice*(1-discount)) as 'Kazandırdığı para' from `dbo.orders` inner join `dbo.order_details` " +
                                "on `dbo.orders`.OrderID=`dbo.order_details`.OrderID where EmployeeID="+employeeID);
                        while (strs.next()){
                            System.out.println("Şirkete ne kadar para kazandırdığı : "+strs.getDouble(1));
                        }
                    }
                    if(secim==3){
                        strs=st.executeQuery("select ProductName,sum(`dbo.order_details`.Quantity) from `dbo.orders` inner join`dbo.order_details` " +
                                "on `dbo.orders`.OrderID=`dbo.order_details`.OrderID inner join " +
                                "`dbo.Products` on `dbo.order_details`.ProductID=`dbo.Products`.ProductID where EmployeeID="+employeeID+ " group by ProductName");
                        while (strs.next()){
                            System.out.print(strs.getString(1)+"  ");
                            System.out.println(strs.getInt(2));
                        }
                    }
                    if(secim==4){
                        strs=st.executeQuery("select `dbo.orders`.CustomerID,CompanyName,count(`dbo.orders`.CustomerID) as 'ilgi'  from `dbo.orders` inner join " +
                                "`dbo.order_details` on `dbo.orders`.OrderID=`dbo.order_details`.OrderID inner join `dbo.customers`" +
                                " on `dbo.orders`.CustomerID=`dbo.customers`.CustomerID where EmployeeID=" + employeeID+
                                " group by `dbo.orders`.CustomerID,CompanyName order by ilgi desc Limit 1");
                        while (strs.next()){
                            System.out.println("Müşteri ID : " + strs.getString(1));
                            System.out.println("Müşteri adı : " + strs.getString(2));
                            System.out.println("İlgilenme sayısı : " + strs.getString(3));
                        }
                    }
                    if (secim==5){
                        System.out.println("Çıkış yapıldı...");
                        break;
                    }


                    System.out.println();
                }

                catch (Exception e){
                    System.out.println(e);
                    scan.next();
                }

            }
        }catch (Exception exception){
            System.out.println(exception);
        }
    }
}
