/*******************************************************************************
 * Copyright 2018 T145
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package T145.metalchests.config;

import T145.metalchests.api.immutable.RegistryMC;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = RegistryMC.MOD_ID)
@Config(modid = RegistryMC.MOD_ID, category = "", name = "T145/" + RegistryMC.MOD_NAME)
@Config.LangKey(RegistryMC.MOD_ID)
public class ModConfig {

    @Config.LangKey(RegistryMC.MOD_ID + ".config.general")
    public static final CategoryGeneral GENERAL = new CategoryGeneral();

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(RegistryMC.MOD_ID)) {
            ConfigManager.sync(RegistryMC.MOD_ID, Config.Type.INSTANCE);
        }
    }
}
