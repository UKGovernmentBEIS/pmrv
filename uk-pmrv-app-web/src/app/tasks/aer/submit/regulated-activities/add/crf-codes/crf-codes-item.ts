type CrfCategory =
  | '_1_A_1'
  | '_1_A_2'
  | '_1_A_3'
  | '_1_A_4'
  | '_1_A_5'
  | '_1_B_1'
  | '_1_B_2'
  | '_2_A'
  | '_2_B'
  | '_2_C'
  | '_2_D'
  | '_2_E'
  | '_2_F'
  | '_2_G';
type CrfCode =
  | '_1_A_1_A'
  | '_1_A_1_B'
  | '_1_A_1_C'
  | '_1_A_2_A'
  | '_1_A_2_B'
  | '_1_A_2_C'
  | '_1_A_2_D'
  | '_1_A_2_E'
  | '_1_A_2_F'
  | '_1_A_2_GVIII'
  | '_1_A_2_GVII'
  | '_1_A_3_AII'
  | '_1_A_3_AI'
  | '_1_A_3_B'
  | '_1_A_3_C'
  | '_1_A_3_DII'
  | '_1_A_3_DI'
  | '_1_A_3_E'
  | '_1_A_4_A'
  | '_1_A_4_B'
  | '_1_A_4_C'
  | '_1_A_5_A'
  | '_1_A_5_B'
  | '_1_B_1_A'
  | '_1_B_1_B'
  | '_1_B_1_C'
  | '_1_B_2_A'
  | '_1_B_2_B'
  | '_1_B_2_C'
  | '_2_A_1'
  | '_2_A_2'
  | '_2_A_3'
  | '_2_A_4'
  | '_2_B_10'
  | '_2_B_1'
  | '_2_B_2'
  | '_2_B_3'
  | '_2_B_4'
  | '_2_B_5'
  | '_2_B_6'
  | '_2_B_7'
  | '_2_B_8'
  | '_2_B_9'
  | '_2_C_1'
  | '_2_C_2'
  | '_2_C_3'
  | '_2_C_4'
  | '_2_C_5'
  | '_2_C_6'
  | '_2_C_7'
  | '_2_D_1'
  | '_2_D_2'
  | '_2_D_3'
  | '_2_E_1'
  | '_2_E_2'
  | '_2_E_3'
  | '_2_E_4'
  | '_2_E_5'
  | '_2_F_1'
  | '_2_F_2'
  | '_2_F_3'
  | '_2_F_4'
  | '_2_F_5'
  | '_2_F_6'
  | '_2_G_1'
  | '_2_G_2'
  | '_2_G_3'
  | '_2_G_4';

export const activityItemNameMap: Record<CrfCategory | CrfCode, string> = {
  _1_A_1: 'Energy Industries',
  _1_A_1_A: '1.A.1.a Public Electricity and Heat Production',
  _1_A_1_B: '1.A.1.b Petroleum refining',
  _1_A_1_C: '1.A.1.c Manufacture of Solid Fuels and Other Energy Industries',
  _1_A_2: 'Manufacturing Industries and Construction',
  _1_A_2_A: '1.A.2.a Iron and Steel',
  _1_A_2_B: '1.A.2.b Non-ferrous Metals',
  _1_A_2_C: '1.A.2.c Chemicals',
  _1_A_2_D: '1.A.2.d Pulp, Paper and Print',
  _1_A_2_E: '1.A.2.e Food Processing, Beverages and Tobacco',
  _1_A_2_F: '1.A.2.f Non-metallic minerals',
  _1_A_2_GVII: '1.A.2.gvii Mobile combustion in manufacturing industries and construction',
  _1_A_2_GVIII: '1.A.2.gviii Stationary combustion in manufacturing and construction: Other',
  _1_A_3: 'Transport',
  _1_A_3_AI: '1.A.3.ai International Aviation',
  _1_A_3_AII: '1.A.3.aii Civil Aviation',
  _1_A_3_B: '1.A.3.b Road Transportation',
  _1_A_3_C: '1.A.3.c Railways',
  _1_A_3_DI: '1.A.3.di International Navigation',
  _1_A_3_DII: '1.A.3.dii National Navigation',
  _1_A_3_E: '1.A.3.e Other',
  _1_A_4: 'Other sectors',
  _1_A_4_A: '1.A.4.a Commercial / Institutional Combustion',
  _1_A_4_B: '1.A.4.b Residential',
  _1_A_4_C: '1.A.4.c Agriculture / Forestry / Fishing',
  _1_A_5: 'Other (not elsewhere specified)',
  _1_A_5_A: '1.A.5.a Other, Stationary (including Military)',
  _1_A_5_B: '1.A.5.b Other, Mobile (including military)',
  _1_B_1: 'Fugitive Emissions from Solid Fuels',
  _1_B_1_A: '1.B.1.a Coal Mining and Handling',
  _1_B_1_B: '1.B.1.b Solid fuel transformation',
  _1_B_1_C: '1.B.1.c Other',
  _1_B_2: 'Oil and natural gas',
  _1_B_2_A: '1.B.2.a Oil',
  _1_B_2_B: '1.B.2.b Natural gas',
  _1_B_2_C: '1.B.2.c Venting and flaring',
  _2_A: 'Mineral Products',
  _2_A_1: '2.A.1 Cement Production',
  _2_A_2: '2.A.2 Lime Production',
  _2_A_3: '2.A.3 Glass Production',
  _2_A_4: '2.A.4 Other Process uses of Carbonates',
  _2_B: 'Chemical Industry',
  _2_B_1: '2.B.1 Ammonia Production',
  _2_B_2: '2.B.2 Nitric Acid Production',
  _2_B_3: '2.B.3 Adipic Acid Production',
  _2_B_4: '2.B.4 Caprolactam, Glyoxal and Glyoxylic Acid Production',
  _2_B_5: '2.B.5 Carbide production',
  _2_B_6: '2.B.6 Titanium Dioxide Production',
  _2_B_7: '2.B.7 Soda Ash Production',
  _2_B_8: '2.B.8 Petrochemical and Carbon Black Production',
  _2_B_9: '2.B.9 Fluorochemical Production',
  _2_B_10: '2.B.10 Other',
  _2_C: 'Metal Production',
  _2_C_1: '2.C.1 Iron and Steel production',
  _2_C_2: '2.C.2 Ferroalloys Production',
  _2_C_3: '2.C.3 Aluminium Production',
  _2_C_4: '2.C.4 Magnesium Production',
  _2_C_5: '2.C.5 Lead Production',
  _2_C_6: '2.C.6 Zinc Production',
  _2_C_7: '2.C.7 Other Metal Production',
  _2_D: 'Non-energy Products from Fuels and Solvent Use',
  _2_D_1: '2.D.1 Lubricant Use',
  _2_D_2: '2.D.2. Paraffin Wax Use',
  _2_D_3: '2.D.3. Other',
  _2_E: 'Electronics Industry',
  _2_E_1: '2.E.1 Integrated Circuit or Semiconductor',
  _2_E_2: '2.E.2 TFT Flat Panel Display',
  _2_E_3: '2.E.3 Photovoltaics',
  _2_E_4: '2.E.4 Heat Transfer Fluid',
  _2_E_5: '2.E.5 Other',
  _2_F: 'Product Uses as Substitutes for ODS',
  _2_F_1: '2.F.1 Refrigeration and Air Conditioning Equipment',
  _2_F_2: '2.F.2 Foam Blowing Agents',
  _2_F_3: '2.F.3 Fire Extinguishers',
  _2_F_4: '2.F.4 Aerosols',
  _2_F_5: '2.F.5 Solvents',
  _2_F_6: '2.F.6 Other',
  _2_G: 'Other Product Manufacture and Use',
  _2_G_1: '2.G.1 Electrical Equipment',
  _2_G_2: '2.G.2 SF6 and PFCs from Other Product Use',
  _2_G_3: '2.G.3 N2O from Product Uses',
  _2_G_4: '2.G.4 Other',
};
