package org.sycamore.llm.hub.frameworks.proxy.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.sycamore.llm.hub.frameworks.proxy.config.NettyServerProperties;

/**
 * @author: Sycamore
 * @date: 2024/4/15 15:38
 * @version: 1.0
 * @description: netty proxy server app start
 */
@Slf4j(topic = "proxy server app")
@RequiredArgsConstructor
public class MainApp implements ApplicationRunner{
    private final NettyServerProperties nettyServerProperties;
    private final MainServer mainServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("proxy server app starting");
        log.info("\n"+
                " ██▓███   ██▀███   ▒█████  ▒██   ██▒▓██   ██▓     ██████ ▓█████  ██▀███   ██▒   █▓▓█████  ██▀███      ▄▄▄       ██▓███   ██▓███  \n" +
                "▓██░  ██▒▓██ ▒ ██▒▒██▒  ██▒▒▒ █ █ ▒░ ▒██  ██▒   ▒██    ▒ ▓█   ▀ ▓██ ▒ ██▒▓██░   █▒▓█   ▀ ▓██ ▒ ██▒   ▒████▄    ▓██░  ██▒▓██░  ██▒\n" +
                "▓██░ ██▓▒▓██ ░▄█ ▒▒██░  ██▒░░  █   ░  ▒██ ██░   ░ ▓██▄   ▒███   ▓██ ░▄█ ▒ ▓██  █▒░▒███   ▓██ ░▄█ ▒   ▒██  ▀█▄  ▓██░ ██▓▒▓██░ ██▓▒\n" +
                "▒██▄█▓▒ ▒▒██▀▀█▄  ▒██   ██░ ░ █ █ ▒   ░ ▐██▓░     ▒   ██▒▒▓█  ▄ ▒██▀▀█▄    ▒██ █░░▒▓█  ▄ ▒██▀▀█▄     ░██▄▄▄▄██ ▒██▄█▓▒ ▒▒██▄█▓▒ ▒\n" +
                "▒██▒ ░  ░░██▓ ▒██▒░ ████▓▒░▒██▒ ▒██▒  ░ ██▒▓░   ▒██████▒▒░▒████▒░██▓ ▒██▒   ▒▀█░  ░▒████▒░██▓ ▒██▒    ▓█   ▓██▒▒██▒ ░  ░▒██▒ ░  ░\n" +
                "▒▓▒░ ░  ░░ ▒▓ ░▒▓░░ ▒░▒░▒░ ▒▒ ░ ░▓ ░   ██▒▒▒    ▒ ▒▓▒ ▒ ░░░ ▒░ ░░ ▒▓ ░▒▓░   ░ ▐░  ░░ ▒░ ░░ ▒▓ ░▒▓░    ▒▒   ▓▒█░▒▓▒░ ░  ░▒▓▒░ ░  ░\n" +
                "░▒ ░       ░▒ ░ ▒░  ░ ▒ ▒░ ░░   ░▒ ░ ▓██ ░▒░    ░ ░▒  ░ ░ ░ ░  ░  ░▒ ░ ▒░   ░ ░░   ░ ░  ░  ░▒ ░ ▒░     ▒   ▒▒ ░░▒ ░     ░▒ ░     \n" +
                "░░         ░░   ░ ░ ░ ░ ▒   ░    ░   ▒ ▒ ░░     ░  ░  ░     ░     ░░   ░      ░░     ░     ░░   ░      ░   ▒   ░░       ░░       \n" +
                "            ░         ░ ░   ░    ░   ░ ░              ░     ░  ░   ░           ░     ░  ░   ░              ░  ░                  \n" +
                " ▄▄▄▄ ▓██   ██▓        ██████▓██   ██▓ ▄████▄   ▄▄▄       ███▄ ▄███▓ ▒█████   ██▀███  ▓█████                                     \n" +
                "▓█████▄▒██  ██▒      ▒██    ▒ ▒██  ██▒▒██▀ ▀█  ▒████▄    ▓██▒▀█▀ ██▒▒██▒  ██▒▓██ ▒ ██▒▓█   ▀                                     \n" +
                "▒██▒ ▄██▒██ ██░      ░ ▓██▄    ▒██ ██░▒▓█    ▄ ▒██  ▀█▄  ▓██    ▓██░▒██░  ██▒▓██ ░▄█ ▒▒███                                       \n" +
                "▒██░█▀  ░ ▐██▓░        ▒   ██▒ ░ ▐██▓░▒▓▓▄ ▄██▒░██▄▄▄▄██ ▒██    ▒██ ▒██   ██░▒██▀▀█▄  ▒▓█  ▄                                     \n" +
                "░▓█  ▀█▓░ ██▒▓░      ▒██████▒▒ ░ ██▒▓░▒ ▓███▀ ░ ▓█   ▓██▒▒██▒   ░██▒░ ████▓▒░░██▓ ▒██▒░▒████▒                                    \n" +
                "░▒▓███▀▒ ██▒▒▒       ▒ ▒▓▒ ▒ ░  ██▒▒▒ ░ ░▒ ▒  ░ ▒▒   ▓▒█░░ ▒░   ░  ░░ ▒░▒░▒░ ░ ▒▓ ░▒▓░░░ ▒░ ░                                    \n" +
                "▒░▒   ░▓██ ░▒░       ░ ░▒  ░ ░▓██ ░▒░   ░  ▒     ▒   ▒▒ ░░  ░      ░  ░ ▒ ▒░   ░▒ ░ ▒░ ░ ░  ░                                    \n" +
                " ░    ░▒ ▒ ░░        ░  ░  ░  ▒ ▒ ░░  ░          ░   ▒   ░      ░   ░ ░ ░ ▒    ░░   ░    ░                                       \n" +
                " ░     ░ ░                 ░  ░ ░     ░ ░            ░  ░       ░       ░ ░     ░        ░  ░                                    \n" +
                "      ░░ ░                    ░ ░     ░                                                                                          "
                +"\n");

        mainServer.start();

        log.info("proxy server app started success, server properties info --> {}", nettyServerProperties.toString());
    }
}
