/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.configs;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.configs.CommonsConfig;
import com.aionemu.commons.configs.DatabaseConfig;
import com.aionemu.commons.configuration.ConfigurableProcessor;
import com.aionemu.commons.utils.PropertiesUtils;
import com.aionemu.gameserver.configs.administration.*;
import com.aionemu.gameserver.configs.main.*;
import com.aionemu.gameserver.configs.network.*;

public class Config
{
	protected static final Logger log = LoggerFactory.getLogger(Config.class);
	
	public static void load() {
		try {
			Properties myProps = null;
			try {
				log.info("Loading: mygs.properties");
				myProps = PropertiesUtils.load("./config/mygs.properties");
			} catch (Exception e) {
				log.info("No override properties found");
			}
			String administration = "./config/administration";
			Properties[] adminProps = PropertiesUtils.loadAllFromDirectory(administration);
			PropertiesUtils.overrideProperties(adminProps, myProps);
			ConfigurableProcessor.process(AdminConfig.class, adminProps);
			ConfigurableProcessor.process(DeveloperConfig.class, adminProps);
			ConfigurableProcessor.process(PanelConfig.class, adminProps);
			String main = "./config/main";
			Properties[] mainProps = PropertiesUtils.loadAllFromDirectory(main);
			PropertiesUtils.overrideProperties(mainProps, myProps);
			ConfigurableProcessor.process(AIConfig.class, mainProps);
			ConfigurableProcessor.process(BrokerConfig.class, mainProps);
			ConfigurableProcessor.process(CommonsConfig.class, mainProps);
			ConfigurableProcessor.process(CacheConfig.class, mainProps);
			ConfigurableProcessor.process(CleaningConfig.class, mainProps);
			ConfigurableProcessor.process(CraftConfig.class, mainProps);
			ConfigurableProcessor.process(CustomConfig.class, mainProps);
			ConfigurableProcessor.process(CustomRewardAgentConfig.class, mainProps);
			ConfigurableProcessor.process(WarConfig.class, mainProps);
			ConfigurableProcessor.process(DropConfig.class, mainProps);
			ConfigurableProcessor.process(EnchantsConfig.class, mainProps);
			ConfigurableProcessor.process(EventsConfig.class, mainProps);
			ConfigurableProcessor.process(FallDamageConfig.class, mainProps);
			ConfigurableProcessor.process(AStationConfig.class, mainProps);
			ConfigurableProcessor.process(GSConfig.class, mainProps);
			ConfigurableProcessor.process(GeoDataConfig.class, mainProps);
			ConfigurableProcessor.process(GroupConfig.class, mainProps);
			ConfigurableProcessor.process(HousingConfig.class, mainProps);
			ConfigurableProcessor.process(HTMLConfig.class, mainProps);
			ConfigurableProcessor.process(InGameShopConfig.class, mainProps);
			ConfigurableProcessor.process(AbyssLandingConfig.class, mainProps);
			ConfigurableProcessor.process(LegionConfig.class, mainProps);
			ConfigurableProcessor.process(LoggingConfig.class, mainProps);
			ConfigurableProcessor.process(MembershipConfig.class, mainProps);
			ConfigurableProcessor.process(NameConfig.class, mainProps);
			ConfigurableProcessor.process(PeriodicSaveConfig.class, mainProps);
			ConfigurableProcessor.process(PlayerTransferConfig.class, mainProps);
			ConfigurableProcessor.process(PricesConfig.class, mainProps);
			ConfigurableProcessor.process(PunishmentConfig.class, mainProps);
			ConfigurableProcessor.process(PvPConfig.class, mainProps);
			ConfigurableProcessor.process(RankingConfig.class, mainProps);
			ConfigurableProcessor.process(RateConfig.class, mainProps);
			ConfigurableProcessor.process(SecurityConfig.class, mainProps);
			ConfigurableProcessor.process(ShutdownConfig.class, mainProps);
			ConfigurableProcessor.process(SiegeConfig.class, mainProps);
			ConfigurableProcessor.process(ThreadConfig.class, mainProps);
			ConfigurableProcessor.process(WeddingsConfig.class, mainProps);
			ConfigurableProcessor.process(WorldConfig.class, mainProps);
			ConfigurableProcessor.process(AdvCustomConfig.class, mainProps);
			ConfigurableProcessor.process(AutoGroupConfig.class, mainProps);
			ConfigurableProcessor.process(PvPModConfig.class, mainProps);
			ConfigurableProcessor.process(NewbieGuide.class, mainProps);
			String network = "./config/network";
			Properties[] networkProps = PropertiesUtils.loadAllFromDirectory(network);
			PropertiesUtils.overrideProperties(networkProps, myProps);
			ConfigurableProcessor.process(DatabaseConfig.class, networkProps);
			ConfigurableProcessor.process(NetworkConfig.class, networkProps);
		} catch (Exception e) {
			log.error("Can't load gameserver configuration: ", e);
			throw new Error("Can't load gameserver configuration: ", e);
		}
		IPConfig.load();
	}
	
	public static void reload() {
		try {
			Properties myProps = null;
			try {
				log.info("Loading: mygs.properties");
				myProps = PropertiesUtils.load("./config/mygs.properties");
			} catch (Exception e) {
				log.info("No override properties found");
			}
			String administration = "./config/administration";
			Properties[] adminProps = PropertiesUtils.loadAllFromDirectory(administration);
			PropertiesUtils.overrideProperties(adminProps, myProps);
			ConfigurableProcessor.process(AdminConfig.class, adminProps);
			ConfigurableProcessor.process(DeveloperConfig.class, adminProps);
			ConfigurableProcessor.process(PanelConfig.class, adminProps);
			String main = "./config/main";
			Properties[] mainProps = PropertiesUtils.loadAllFromDirectory(main);
			PropertiesUtils.overrideProperties(mainProps, myProps);
			ConfigurableProcessor.process(AIConfig.class, mainProps);
			ConfigurableProcessor.process(BrokerConfig.class, mainProps);
			ConfigurableProcessor.process(CommonsConfig.class, mainProps);
			ConfigurableProcessor.process(CacheConfig.class, mainProps);
			ConfigurableProcessor.process(CleaningConfig.class, mainProps);
			ConfigurableProcessor.process(CraftConfig.class, mainProps);
			ConfigurableProcessor.process(CustomConfig.class, mainProps);
			ConfigurableProcessor.process(CustomRewardAgentConfig.class, mainProps);
			ConfigurableProcessor.process(WarConfig.class, mainProps);
			ConfigurableProcessor.process(DropConfig.class, mainProps);
			ConfigurableProcessor.process(EnchantsConfig.class, mainProps);
			ConfigurableProcessor.process(EventsConfig.class, mainProps);
			ConfigurableProcessor.process(FallDamageConfig.class, mainProps);
			ConfigurableProcessor.process(AStationConfig.class, mainProps);
			ConfigurableProcessor.process(GSConfig.class, mainProps);
			ConfigurableProcessor.process(GeoDataConfig.class, mainProps);
			ConfigurableProcessor.process(GroupConfig.class, mainProps);
			ConfigurableProcessor.process(HousingConfig.class, mainProps);
			ConfigurableProcessor.process(HTMLConfig.class, mainProps);
			ConfigurableProcessor.process(InGameShopConfig.class, mainProps);
			ConfigurableProcessor.process(AbyssLandingConfig.class, mainProps);
			ConfigurableProcessor.process(LegionConfig.class, mainProps);
			ConfigurableProcessor.process(LoggingConfig.class, mainProps);
			ConfigurableProcessor.process(MembershipConfig.class, mainProps);
			ConfigurableProcessor.process(NameConfig.class, mainProps);
			ConfigurableProcessor.process(PeriodicSaveConfig.class, mainProps);
			ConfigurableProcessor.process(PlayerTransferConfig.class, mainProps);
			ConfigurableProcessor.process(PricesConfig.class, mainProps);
			ConfigurableProcessor.process(PunishmentConfig.class, mainProps);
			ConfigurableProcessor.process(PvPConfig.class, mainProps);
			ConfigurableProcessor.process(RankingConfig.class, mainProps);
			ConfigurableProcessor.process(RateConfig.class, mainProps);
			ConfigurableProcessor.process(SecurityConfig.class, mainProps);
			ConfigurableProcessor.process(ShutdownConfig.class, mainProps);
			ConfigurableProcessor.process(SiegeConfig.class, mainProps);
			ConfigurableProcessor.process(ThreadConfig.class, mainProps);
			ConfigurableProcessor.process(WeddingsConfig.class, mainProps);
			ConfigurableProcessor.process(WorldConfig.class, mainProps);
			ConfigurableProcessor.process(AdvCustomConfig.class, mainProps);
			ConfigurableProcessor.process(AutoGroupConfig.class, mainProps);
			ConfigurableProcessor.process(PvPModConfig.class, mainProps);
			ConfigurableProcessor.process(NewbieGuide.class, mainProps);
		} catch (Exception e) {
			log.error("Can't reload configuration: ", e);
			throw new Error("Can't reload configuration: ", e);
		}
	}
}