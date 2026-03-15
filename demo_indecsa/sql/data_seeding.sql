-- ============================================================
--  INDECSA · Data Seeding v3.0
--  Compatible con: indecsa_v3_completo.sql
--  Fecha: 2026-03-14
-- ============================================================
-- NOTAS:
--  · La tabla Privilegio no existe en v3 → su seeding se omite.
--  · Las contraseñas están en texto plano solo para pruebas;
--    en producción deben ser hashes (bcrypt / argon2).
--  · Los id_rol se resuelven por nombre_rol para no depender
--    del valor AUTO_INCREMENT generado.
--  · El orden respeta las dependencias de FK:
--      Rol → Empleado
--      Contratista → Asignacion_Proyecto_Contratista
--      Proyecto    → Asignacion_Proyecto_Contratista
--      Asignacion_Proyecto_Contratista → Asignacion_Trabajador_Proyecto
--      Trabajador  → Asignacion_Trabajador_Proyecto
-- ============================================================
USE indecsa;


-- ===================================
-- 1. ROL
-- ===================================
-- El ENUM de Rol solo acepta: 'ADMIN', 'CAPITAL_HUMANO', 'SUPERVISOR'
INSERT INTO Rol (nombre_rol, descripcion_rol) VALUES
    ('ADMIN',
    'Administrador del sistema con acceso a modulos de contratistas y proyectos'),
    ('CAPITAL_HUMANO',
    'Gestion de personal interno, nomina y reclutamiento'),
    ('SUPERVISOR',
    'Supervision de obras y control de asignaciones de trabajadores');


-- ===================================
-- 2. EMPLEADO
-- ===================================
-- Se resuelve id_rol por subquery para evitar asumir el AUTO_INCREMENT.
INSERT INTO Empleado (nombre_empleado, correo_empleado, contrasena, id_rol) VALUES
    ('Admin Principal',
    'admin@indecsa.com',
    'Admin_unico_123',
    (SELECT id_rol FROM Rol WHERE nombre_rol = 'ADMIN')),

    ('Laura Ramirez',
    'laura.ramirez@indecsa.com',
    'CapHum_456',
    (SELECT id_rol FROM Rol WHERE nombre_rol = 'CAPITAL_HUMANO')),

    ('Carlos Mendoza',
    'carlos.mendoza@indecsa.com',
    'Supervisor_789',
    (SELECT id_rol FROM Rol WHERE nombre_rol = 'SUPERVISOR'));


-- ===================================
-- 3. CONTRATISTA
-- ===================================
INSERT INTO Contratista
(rfc_contratista, telefono_contratista, correo_contratista,
 contrasenia_contratista, descripcion_contratista,
 experiencia, calificacion_contratista, estado_contratista)
VALUES
    ('CONSA800101AA1', '5551234567',
     'contacto@constructoranorte.com',
     'Cnorte_pass1',
     'Empresa especializada en obra civil e infraestructura urbana',
     '15 años en construccion de carreteras y puentes', 5, 'ACTIVO'),

    ('EDIFE900215BB2', '5559876543',
     'info@edificacionessur.com',
     'Esur_pass2',
     'Contratista de edificaciones residenciales y comerciales',
     '10 años en proyectos habitacionales', 4, 'ACTIVO'),

    ('INSOS751130CC3', '5554445566',
     'ventas@instalacionesoscar.com',
     'Ioscar_pass3',
     'Especialista en instalaciones electricas e hidraulicas',
     '8 años en instalaciones industriales', 3, 'INACTIVO');


-- ===================================
-- 4. TRABAJADOR
-- ===================================
INSERT INTO Trabajador
(nombre_trabajador, nss_trabajador, experiencia,
 telefono_trabajador, correo_trabajador, contrasenia_trabajador,
 especialidad_trabajador, estado_trabajador,
 descripcion_trabajador, calificacion_trabajador, fecha_ingreso)
VALUES
    ('Jorge Luis Perez',   '12345678901', '5 años en colado de losas y cimentaciones',
     '5551112233', 'jorge.perez@trabajador.com',   'Trab_pass1',
     'Albanil',     'ACTIVO',  'Trabajador puntual con experiencia en obra gruesa', 5, '2021-03-01'),

    ('Maria Elena Torres', '23456789012', '3 años en instalaciones electricas residenciales',
     '5552223344', 'maria.torres@trabajador.com',  'Trab_pass2',
     'Electricista', 'ACTIVO', 'Certificada en norma NOM-001-SEDE',               4, '2022-07-15'),

    ('Roberto Salas',      '34567890123', '7 años en plomeria industrial',
     '5553334455', 'roberto.salas@trabajador.com', 'Trab_pass3',
     'Plomero',     'ACTIVO',  'Especialista en redes hidraulicas a presion',      4, '2020-01-10'),

    ('Ana Gomez',          '45678901234', '2 años en acabados y pintura',
     '5554445566', 'ana.gomez@trabajador.com',     'Trab_pass4',
     'Acabados',    'VACACIONES', 'Experiencia en proyectos residenciales de lujo', 3, '2023-02-20'),

    ('Luis Hernandez',     '56789012345', '1 año en topografia',
     '5555556677', 'luis.hernandez@trabajador.com','Trab_pass5',
     'Topografo',   'BAJA',    'Dado de baja por termino de contrato',             2, '2024-01-05');


-- ===================================
-- 5. PROYECTO
-- ===================================
INSERT INTO Proyecto
(nombre_proyecto, tipo_proyecto, lugar_proyecto,
 municipio_proyecto, estado_proyecto_geo,
 fecha_estimada_inicio, fecha_estimada_fin,
 calificacion_proyecto, estatus_proyecto, descripcion_proyecto)
VALUES
    ('Ampliacion Carretera Federal 57',
     'Infraestructura vial', 'Km 142, Carretera 57',
     'San Luis Potosi', 'San Luis Potosi',
     '2026-02-01', '2026-11-30',
     NULL, 'EN_CURSO',
     'Ampliacion a cuatro carriles del tramo norte de la federal 57'),

    ('Complejo Habitacional Las Palmas',
     'Edificacion residencial', 'Fracc. Las Palmas',
     'Queretaro', 'Queretaro',
     '2026-04-01', '2027-03-31',
     NULL, 'PLANEACION',
     'Construccion de 120 viviendas de interes social con areas comunes'),

    ('Planta Industrial Zona Norte',
     'Obra industrial', 'Parque Industrial Norte',
     'Monterrey', 'Nuevo Leon',
     '2025-06-01', '2026-01-15',
     4, 'FINALIZADO',
     'Construccion de nave industrial de 5000 m2 con instalaciones especiales');


-- ===================================
-- 6. ASIGNACION PROYECTO - CONTRATISTA
-- ===================================
-- Solo se asignan contratistas ACTIVOS a proyectos no CANCELADOS
-- (los triggers trg_bloquear_contratista_inactivo y
--  trg_bloquear_asignacion_proyecto_inactivo lo validarian en runtime;
--  aqui los datos ya son coherentes).
INSERT INTO Asignacion_Proyecto_Contratista
(id_proyecto, id_contratista, numero_contrato,
 fecha_inicio, fecha_fin_estimada,
 monto_contratado, estatus_contrato, observaciones)
VALUES
    -- Contratista Norte → Proyecto Carretera (EN_CURSO)
    ((SELECT id_proyecto FROM Proyecto WHERE nombre_proyecto = 'Ampliacion Carretera Federal 57'),
     (SELECT id_contratista FROM Contratista WHERE rfc_contratista = 'CONSA800101AA1'),
     'CONT-2026-001',
     '2026-02-01', '2026-11-30',
     4500000.00, 'ACTIVO',
     'Contrato principal de obra civil para ampliacion vial'),

    -- Edificaciones Sur → Proyecto Habitacional (PLANEACION)
    ((SELECT id_proyecto FROM Proyecto WHERE nombre_proyecto = 'Complejo Habitacional Las Palmas'),
     (SELECT id_contratista FROM Contratista WHERE rfc_contratista = 'EDIFE900215BB2'),
     'CONT-2026-002',
     '2026-04-01', '2027-03-31',
     9800000.00, 'ACTIVO',
     'Contrato de edificacion para el complejo residencial'),

    -- Contratista Norte → Planta Industrial (FINALIZADO)
    ((SELECT id_proyecto FROM Proyecto WHERE nombre_proyecto = 'Planta Industrial Zona Norte'),
     (SELECT id_contratista FROM Contratista WHERE rfc_contratista = 'CONSA800101AA1'),
     'CONT-2025-010',
     '2025-06-01', '2026-01-15',
     12000000.00, 'FINALIZADO',
     'Contrato concluido satisfactoriamente');


-- ===================================
-- 7. ASIGNACION TRABAJADOR - PROYECTO
-- ===================================
-- Solo trabajadores ACTIVOS y proyectos activos (no FINALIZADO/CANCELADO).
-- Ana Gomez (VACACIONES) y Luis Hernandez (BAJA) quedan excluidos
-- porque el trigger trg_bloquear_trabajador_inactivo rechaza BAJA e INACTIVO;
-- VACACIONES si es permitido pero se omite intencionalmente aqui para claridad.
-- El id_asignacion_pc debe pertenecer al mismo id_proyecto (valida trigger T1).
INSERT INTO Asignacion_Trabajador_Proyecto
(id_trabajador, id_proyecto, id_asignacion_pc,
 rol_en_proyecto, fecha_inicio, fecha_fin_estimada,
 estatus_asignacion, observaciones)
VALUES
    -- Jorge Perez → Carretera Federal (albanil, contrato CONT-2026-001)
    ((SELECT id_trabajador FROM Trabajador WHERE correo_trabajador = 'jorge.perez@trabajador.com'),
     (SELECT id_proyecto   FROM Proyecto    WHERE nombre_proyecto  = 'Ampliacion Carretera Federal 57'),
     (SELECT id_asignacion_pc FROM Asignacion_Proyecto_Contratista WHERE numero_contrato = 'CONT-2026-001'),
     'Albanil de cimentacion',
     '2026-02-10', '2026-11-30',
     'ACTIVO', 'Asignado a cuadrilla de colado zona norte'),

    -- Maria Torres → Carretera Federal (electricista, mismo contrato)
    ((SELECT id_trabajador FROM Trabajador WHERE correo_trabajador = 'maria.torres@trabajador.com'),
     (SELECT id_proyecto   FROM Proyecto    WHERE nombre_proyecto  = 'Ampliacion Carretera Federal 57'),
     (SELECT id_asignacion_pc FROM Asignacion_Proyecto_Contratista WHERE numero_contrato = 'CONT-2026-001'),
     'Electricista de campo',
     '2026-03-01', '2026-11-30',
     'ACTIVO', 'Responsable de alumbrado publico del tramo'),

    -- Roberto Salas → Complejo Habitacional (plomero, contrato CONT-2026-002)
    ((SELECT id_trabajador FROM Trabajador WHERE correo_trabajador = 'roberto.salas@trabajador.com'),
     (SELECT id_proyecto   FROM Proyecto    WHERE nombre_proyecto  = 'Complejo Habitacional Las Palmas'),
     (SELECT id_asignacion_pc FROM Asignacion_Proyecto_Contratista WHERE numero_contrato = 'CONT-2026-002'),
     'Plomero jefe de instalaciones',
     '2026-04-05', '2027-03-31',
     'ACTIVO', 'Lider de la red hidraulica del complejo');