import { addCollection } from "@iconify/vue";

// 导入常用图标集
import epIcons from "@iconify/json/json/ep.json";
import mdiIcons from "@iconify/json/json/mdi.json";
import biIcons from "@iconify/json/json/bi.json";
import carbonIcons from "@iconify/json/json/carbon.json";
import lineMdIcons from "@iconify/json/json/line-md.json";

// 注册图标集到 Iconify
export function setupIconify() {
  addCollection(epIcons);
  addCollection(mdiIcons);
  addCollection(biIcons);
  addCollection(carbonIcons);
  addCollection(lineMdIcons);
}
