final class DosyaHatasi extends Exception { 
    DosyaHatasi(String dosya,int satir,String neden){super(dosya+" satir "+satir+": "+neden);
    } 
}
