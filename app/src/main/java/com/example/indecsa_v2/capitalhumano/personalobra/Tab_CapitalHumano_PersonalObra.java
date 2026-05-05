package com.example.indecsa_v2.capitalhumano.personalobra;

import com.example.indecsa_v2.admin.personalobra.Tab_Admin_PersonalObra;

/**
 * Tab de Personal en Obra para el panel CAPITAL_HUMANO.
 *
 * Comparte el comportamiento del fragment de admin porque, según la matriz de
 * permisos del backend, ambos roles consultan la misma información del módulo de
 * rendimiento. Si más adelante divergen las acciones permitidas, esta clase es
 * el punto donde se sobreescriben los hooks específicos.
 */
public class Tab_CapitalHumano_PersonalObra extends Tab_Admin_PersonalObra {
    public Tab_CapitalHumano_PersonalObra() { }
}
