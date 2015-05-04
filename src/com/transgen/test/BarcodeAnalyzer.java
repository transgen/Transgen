package com.transgen.test;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.pdf417.PDF417Reader;
import com.transgen.TransGen;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class BarcodeAnalyzer {

    public static void main(String args[]) throws IOException, NotFoundException, FormatException, ChecksumException {
        TransGen.debug = true;
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        File input =  new File(System.getProperty("user.dir") + "/Barcodes/021/AK.jpg");
        BufferedImageLuminanceSource bils = new BufferedImageLuminanceSource(ImageIO.read(new File(System.getProperty("user.dir") + "\\Barcodes\\O21\\IN.jpg")));
        HybridBinarizer hb = new HybridBinarizer(bils);
        BinaryBitmap bitmap = new BinaryBitmap(hb);

        PDF417Reader read = new PDF417Reader();
        Result res = read.decode(bitmap);


        /*for(ResultPoint rp : res.getResultPoints()) {
            System.out.println("(" + rp.getX() + ", " + rp.getY() + ")");
        }*/

        System.out.println("ECL: " + res.getResultMetadata().get(ResultMetadataType.ERROR_CORRECTION_LEVEL));

        System.out.println(res.getRawBytes());

        System.out.println("DATA:");
        System.out.println(res.getText());
    }
}
