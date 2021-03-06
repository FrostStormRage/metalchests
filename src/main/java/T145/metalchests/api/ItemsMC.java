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
package T145.metalchests.api;

import T145.metalchests.api.immutable.RegistryMC;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(RegistryMC.MOD_ID)
public class ItemsMC {

    private ItemsMC() {}

    @ObjectHolder(RegistryMC.KEY_CHEST_UPGRADE)
    public static Item CHEST_UPGRADE;

    @ObjectHolder(RegistryMC.KEY_HUNGRY_CHEST_UPGRADE)
    public static Item HUNGRY_CHEST_UPGRADE;

    @ObjectHolder(RegistryMC.KEY_MINECART_METAL_CHEST)
    public static Item MINECART_METAL_CHEST;
}
