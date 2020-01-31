import com.pi4j.io.gpio.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 直接进行对
 */
public class Basic {

    /** 测试用*/
    public static void main(String[] args) {
        String s = "aaaaaaaaaaaaaaa";
        Basic b = new Basic();
        b.display(Basic.toArray(s));
        b.gpio.shutdown();
    }

    // create gpio controller
    final GpioController gpio;

    // provision gpio pin #01 as an output pin and turn on
    private final GpioPinDigitalOutput pin;

    public Basic() {
        gpio = GpioFactory.getInstance();
        pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);
        // set shutdown state for this pin
        pin.setShutdownOptions(true, PinState.LOW);
    }

    private static int[] decomposeChar(char c) {
        int[] a = new int[8], result = new int[8];
        int b = c;
        int length = 0, i;
        //除二取余法化b为2进制
        while (b != 0) {
            a[length] = b % 2;
            length++;
            b /= 2;
        }
        a[length] = b;
        for (i = 8 - 1; i >= 0; i--)
            result[8 - 1 - i] = a[i];
        return result;
    }

    public static ArrayList<Integer> toArray(String chars) {
        ArrayList<Integer> result = new ArrayList<>();
        for (char aChar : chars.toCharArray())
            for (int j = 0; j < 8; j++) {
                int[] a = decomposeChar(aChar);
                result.add(a[j]);
            }
        return result;
    }

    public void display(ArrayList<Integer> a) {
        for (int a1 : a) output(a1);
        try {
            TimeUnit.MICROSECONDS.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void output(int a) {
        try {
            if (a == 0) {
                pin.high(); //引脚设置为高电平
                TimeUnit.NANOSECONDS.sleep(250);
                pin.low();//引脚设置为低电平
                TimeUnit.MICROSECONDS.sleep(1);
            } else {
                pin.high(); //引脚设置为高电平
                TimeUnit.MICROSECONDS.sleep(1);
                pin.low();//引脚设置为低电平
                TimeUnit.NANOSECONDS.sleep(250);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
