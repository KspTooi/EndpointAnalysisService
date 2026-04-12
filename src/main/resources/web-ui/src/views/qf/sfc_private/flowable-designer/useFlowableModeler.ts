import { onBeforeUnmount, ref, shallowRef, type Ref, type ShallowRef } from "vue";
import BpmnModeler from "bpmn-js/lib/Modeler";
import { getFlowableModdleExtensions, buildEmptyFlowableDiagram } from "@/views/qf/sfc_private/flowable-designer/flowableEngine";
import translateModule from "@/views/qf/sfc_private/flowable-designer/translateModule";

export type BpmnElement = {
  id: string;
  type: string;
  businessObject: Record<string, unknown>;
};

export type UseFlowableModelerReturn = {
  modeler: ShallowRef<InstanceType<typeof BpmnModeler> | null>;
  defaultZoom: Ref<number>;
  revocable: Ref<boolean>;
  recoverable: Ref<boolean>;
  init: () => void;
  destroy: () => void;
  importXml: (xml: string) => Promise<void>;
  saveXml: (formatted?: boolean) => Promise<string | undefined>;
  saveSvg: () => Promise<string | undefined>;
  zoomIn: (step?: number) => void;
  zoomOut: (step?: number) => void;
  zoomFit: () => void;
  undo: () => void;
  redo: () => void;
  newDiagram: (processId: string, processName: string) => Promise<void>;
  syncCommandStack: () => void;
};

export function useFlowableModeler(containerRef: Ref<HTMLElement | null>): UseFlowableModelerReturn {
  const modeler = shallowRef<InstanceType<typeof BpmnModeler> | null>(null);
  const defaultZoom = ref(1);
  const revocable = ref(false);
  const recoverable = ref(false);

  const syncCommandStack = (): void => {
    const m = modeler.value;
    if (!m) {
      return;
    }
    const commandStack = m.get("commandStack") as { canUndo: () => boolean; canRedo: () => boolean };
    revocable.value = commandStack.canUndo();
    recoverable.value = commandStack.canRedo();
  };

  const init = (): void => {
    const el = containerRef.value;
    if (!el) {
      return;
    }
    if (modeler.value) {
      return;
    }
    const m = new BpmnModeler({
      container: el,
      keyboard: {},
      additionalModules: [translateModule],
      moddleExtensions: getFlowableModdleExtensions(),
    });
    modeler.value = m;
    const eventBus = m.get("eventBus");
    eventBus.on("commandStack.changed", syncCommandStack);
    m.on("canvas.viewbox.changed", ({ viewbox }: { viewbox: { scale: number } }) => {
      defaultZoom.value = Math.round(viewbox.scale * 100) / 100;
    });
    syncCommandStack();
  };

  const destroy = (): void => {
    const m = modeler.value;
    if (!m) {
      return;
    }
    m.destroy();
    modeler.value = null;
  };

  onBeforeUnmount(() => {
    destroy();
  });

  const importXml = async (xml: string): Promise<void> => {
    const m = modeler.value;
    if (!m) {
      return;
    }
    try {
      const result = await m.importXML(xml);
      if (result.warnings?.length) {
        result.warnings.forEach((w: unknown) => console.warn(w));
      }
    } catch (e) {
      console.error(e);
      throw e;
    }
    syncCommandStack();
  };

  const saveXml = async (formatted = true): Promise<string | undefined> => {
    const m = modeler.value;
    if (!m) {
      return undefined;
    }
    const { xml } = await m.saveXML({ format: formatted });
    return xml as string;
  };

  const saveSvg = async (): Promise<string | undefined> => {
    const m = modeler.value;
    if (!m) {
      return undefined;
    }
    const { svg } = await m.saveSVG();
    return svg;
  };

  const zoomIn = (step = 0.1): void => {
    const m = modeler.value;
    if (!m) {
      return;
    }
    const canvas = m.get("canvas") as { zoom: (v: number) => void };
    const z = Math.min(4, defaultZoom.value + step);
    defaultZoom.value = Math.round(z * 100) / 100;
    canvas.zoom(defaultZoom.value);
  };

  const zoomOut = (step = 0.1): void => {
    const m = modeler.value;
    if (!m) {
      return;
    }
    const canvas = m.get("canvas") as { zoom: (v: number) => void };
    const z = Math.max(0.2, defaultZoom.value - step);
    defaultZoom.value = Math.round(z * 100) / 100;
    canvas.zoom(defaultZoom.value);
  };

  const zoomFit = (): void => {
    const m = modeler.value;
    if (!m) {
      return;
    }
    const canvas = m.get("canvas") as { zoom: (v: string, v2: string) => void };
    defaultZoom.value = 1;
    canvas.zoom("fit-viewport", "auto");
  };

  const undo = (): void => {
    const m = modeler.value;
    if (!m) {
      return;
    }
    (m.get("commandStack") as { undo: () => void }).undo();
  };

  const redo = (): void => {
    const m = modeler.value;
    if (!m) {
      return;
    }
    (m.get("commandStack") as { redo: () => void }).redo();
  };

  const newDiagram = async (processId: string, processName: string): Promise<void> => {
    await importXml(buildEmptyFlowableDiagram(processId, processName));
  };

  return {
    modeler,
    defaultZoom,
    revocable,
    recoverable,
    init,
    destroy,
    importXml,
    saveXml,
    saveSvg,
    zoomIn,
    zoomOut,
    zoomFit,
    undo,
    redo,
    newDiagram,
    syncCommandStack,
  };
}
