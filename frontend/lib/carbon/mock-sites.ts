import type { Site } from './types'

export const mockSites: ReadonlyArray<Site> = [
  {
    id: 'rennes',
    name: 'Rennes Campus (example)',
    areaM2: 11_771,
    parkingSpots: 520,
    employees: 1_800,
    energySources: [
      { type: 'electricity', label: 'Électricité (bâtiment)', annualMWh: 1_500 },
      { type: 'gas', label: 'Gaz (sous-sol)', annualMWh: 340 },
    ],
    materials: [
      { material: 'concrete', tonnes: 22_000 },
      { material: 'steel', tonnes: 1_650 },
      { material: 'glass', tonnes: 420 },
      { material: 'wood', tonnes: 260 },
    ],
    history: [
      {
        year: 2023,
        energySources: [
          { type: 'electricity', label: 'Électricité (bâtiment)', annualMWh: 1_550 },
          { type: 'gas', label: 'Gaz (sous-sol)', annualMWh: 370 },
        ],
        employees: 1_740,
      },
      {
        year: 2024,
        energySources: [
          { type: 'electricity', label: 'Électricité (bâtiment)', annualMWh: 1_520 },
          { type: 'gas', label: 'Gaz (sous-sol)', annualMWh: 360 },
        ],
        employees: 1_780,
      },
      {
        year: 2025,
        energySources: [
          { type: 'electricity', label: 'Électricité (bâtiment)', annualMWh: 1_500 },
          { type: 'gas', label: 'Gaz (sous-sol)', annualMWh: 340 },
        ],
        employees: 1_800,
      },
    ],
  },
  {
    id: 'paris',
    name: 'Paris Site (mock)',
    areaM2: 7_950,
    parkingSpots: 180,
    employees: 1_250,
    energySources: [{ type: 'electricity', label: 'Électricité', annualMWh: 1_420 }],
    materials: [
      { material: 'concrete', tonnes: 14_500 },
      { material: 'steel', tonnes: 1_100 },
      { material: 'glass', tonnes: 520 },
      { material: 'wood', tonnes: 130 },
    ],
    history: [
      {
        year: 2023,
        energySources: [{ type: 'electricity', label: 'Électricité', annualMWh: 1_500 }],
        employees: 1_180,
      },
      {
        year: 2024,
        energySources: [{ type: 'electricity', label: 'Électricité', annualMWh: 1_460 }],
        employees: 1_220,
      },
      {
        year: 2025,
        energySources: [{ type: 'electricity', label: 'Électricité', annualMWh: 1_420 }],
        employees: 1_250,
      },
    ],
  },
  {
    id: 'lyon',
    name: 'Lyon Site (mock)',
    areaM2: 9_300,
    parkingSpots: 340,
    employees: 1_050,
    energySources: [
      { type: 'electricity', label: 'Électricité', annualMWh: 900 },
      { type: 'gas', label: 'Gaz', annualMWh: 260 },
    ],
    materials: [
      { material: 'concrete', tonnes: 18_000 },
      { material: 'steel', tonnes: 1_350 },
      { material: 'glass', tonnes: 300 },
      { material: 'wood', tonnes: 310 },
    ],
    history: [
      {
        year: 2023,
        energySources: [
          { type: 'electricity', label: 'Électricité', annualMWh: 980 },
          { type: 'gas', label: 'Gaz', annualMWh: 260 },
        ],
        employees: 990,
      },
      {
        year: 2024,
        energySources: [
          { type: 'electricity', label: 'Électricité', annualMWh: 940 },
          { type: 'gas', label: 'Gaz', annualMWh: 260 },
        ],
        employees: 1_020,
      },
      {
        year: 2025,
        energySources: [
          { type: 'electricity', label: 'Électricité', annualMWh: 900 },
          { type: 'gas', label: 'Gaz', annualMWh: 260 },
        ],
        employees: 1_050,
      },
    ],
  },
]

