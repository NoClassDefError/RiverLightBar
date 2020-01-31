package exemples;

        import com.pi4j.io.gpio.*;

        import java.util.concurrent.TimeUnit;

public class testGpioExemple {
    public static void main(String [] a) throws InterruptedException {
        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        // provision gpio pin #01 as an output pin and turn on
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);
        while(true){
            pin.high(); //引脚设置为高电平
            TimeUnit.NANOSECONDS.sleep(250);
            pin.low();//引脚设置为低电平
            TimeUnit.MICROSECONDS.sleep(1);
        }
    }
}
