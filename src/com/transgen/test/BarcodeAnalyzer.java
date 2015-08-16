package com.transgen.test;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.common.StringUtils;
import com.google.zxing.pdf417.PDF417Reader;
import com.itextpdf.xmp.impl.Base64;
import com.transgen.TransGen;
import com.transgen.api.StateGenerator;
import com.transgen.api.enums.State;
import com.transgen.test.Test;
import com.transgen.TransGen;
import groovy.lang.GroovyClassLoader;
import com.transgen.Utils;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class BarcodeAnalyzer {

    public static void main(String args[]) throws IOException, NotFoundException, FormatException, ChecksumException {
        TransGen.debug = true;
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        BufferedImageLuminanceSource bils = new BufferedImageLuminanceSource(ImageIO.read(new File(System.getProperty("user.dir") + "\\Barcodes\\O21\\bc.jpg")));
        HybridBinarizer hb = new HybridBinarizer(bils);

        BinaryBitmap bitmap = new BinaryBitmap(hb);

        PDF417Reader read = new PDF417Reader();
        Result res = read.decode(bitmap);



        /*for(ResultPoint rp : res.getResultPoints()) {
            System.out.println("(" + rp.getX() + ", " + rp.getY() + ")");
        }*/

        System.out.println("ECL: " + res.getResultMetadata().get(ResultMetadataType.ERROR_CORRECTION_LEVEL));



        //System.out.println(res.getRawBytes());
        //byte[] bt = res.getRawBytes();
        System.out.println(res.getRawBytes());
        System.out.println(res.getText());
        byte[] converted = Base64.encode(res.getText().getBytes());

        System.out.println(converted.toString());
       try (OutputStream stream = new FileOutputStream("testing/" + "MT" + ".b64")) {
            stream.write(converted);
        }
        TransGen transGen = new TransGen();
        transGen.init();

        System.out.println("DATA:");
        System.out.println(res.getText());
        System.out.println(Utils.repr(res.getText()));

        HashMap < String, Class > stateScripts = transGen.getStateGenerators();
        Test t = new Test(stateScripts.get("MT"));
        System.out.println(t.generateFieldHashMap(State.MONTANA, res.getText()));
        System.out.println(t.generateExampleHash(State.MONTANA, res.getText()));
       System.out.println(t.parseBarcodeData(State.MONTANA, res.getText()));
       t.runTest();
        //t.printStateReport(clazz, res.getText());

    }
}
