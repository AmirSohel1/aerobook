import { Injectable, signal, computed } from '@angular/core';
import {
  GamificationProfile,
  Badge,
  QuestMilestone,
  LevelTier
} from '../models/aerobook.models';

@Injectable({
  providedIn: 'root'
})
export class GamificationService {
  private readonly STORAGE_KEY = 'aerobook_gamification_profile';

  private readonly tiers: LevelTier[] = [
    {
      level: 1,
      title: 'Bronze Wanderer',
      name: 'BRONZE',
      minXp: 0,
      maxXp: 5000,
      color: '#CD7F32',
      badgeIcon: '🥉',
      discountPercentage: 5,
      discountCode: 'BRONZE5',
      perks: [
        '5% off all domestic flight reservations',
        'Standard baggage allowance (7kg Cabin + 15kg Check-in)',
        'Earn 1x AeroMiles on all flights'
      ]
    },
    {
      level: 2,
      title: 'Silver Skyfarer',
      name: 'SILVER',
      minXp: 5000,
      maxXp: 15000,
      color: '#94A3B8',
      badgeIcon: '🥈',
      discountPercentage: 10,
      discountCode: 'SILVER10',
      perks: [
        '10% off all flight reservations',
        'Extra +5kg complimentary baggage allowance',
        'Priority airport check-in counter access',
        'Earn 1.25x AeroMiles accelerator'
      ]
    },
    {
      level: 3,
      title: 'Gold Aviator',
      name: 'GOLD',
      minXp: 15000,
      maxXp: 30000,
      color: '#F59E0B',
      badgeIcon: '👑',
      discountPercentage: 15,
      discountCode: 'GOLD15',
      perks: [
        '15% off all flight reservations (Code: GOLD15)',
        'Complimentary Airport Lounge Access across 30+ airports',
        'Priority Boarding (Boarding Group 2)',
        'Complimentary advance seat selection (Extra Legroom)',
        'Extra +10kg baggage allowance',
        'Earn 1.5x AeroMiles accelerator'
      ]
    },
    {
      level: 4,
      title: 'Platinum Jetsetter',
      name: 'PLATINUM',
      minXp: 30000,
      maxXp: 50000,
      color: '#C084FC',
      badgeIcon: '⚡',
      discountPercentage: 20,
      discountCode: 'PLATINUM20',
      perks: [
        '20% off all flight reservations (Code: PLATINUM20)',
        'Unlimited Airport Partner Lounge & Spa access',
        'Complimentary Business Class upgrade vouchers (2x / yr)',
        'Priority Baggage delivery (First off the carousel)',
        'Zero ticket cancellation / change fees',
        'Earn 2x AeroMiles accelerator'
      ]
    },
    {
      level: 5,
      title: 'Diamond Voyager',
      name: 'DIAMOND',
      minXp: 50000,
      maxXp: 100000,
      color: '#8B5CF6',
      badgeIcon: '💎',
      discountPercentage: 25,
      discountCode: 'DIAMOND25',
      perks: [
        '25% VIP discount across all routes (Code: DIAMOND25)',
        'Guaranteed First / Business Class seat availability',
        'Personal VIP airport concierge escort & tarmac transfers',
        'Unlimited complimentary baggage & sports equipment',
        'Permanent AeroClub Lifetime Diamond recognition'
      ]
    }
  ];

  // Reactive state signal
  readonly profile = signal<GamificationProfile>(this.loadInitialProfile());

  // Computed signals
  readonly currentLevel = computed(() => this.profile().currentLevel);
  readonly currentXp = computed(() => this.profile().currentXp);
  readonly currentTier = computed(() => this.profile().tier);
  readonly levelTitle = computed(() => this.profile().levelTitle);
  readonly progressPercent = computed(() => this.profile().progressPercent);
  readonly xpToNext = computed(() => this.profile().xpToNextLevel);
  readonly unlockedBadges = computed(() => this.profile().badges.filter(b => b.unlocked));
  readonly unlockedDiscounts = computed(() => this.profile().unlockedDiscounts);
  readonly activeQuests = computed(() => this.profile().quests);
  readonly nextTier = computed(() => this.tiers.find(t => t.level === this.profile().currentLevel + 1) || null);

  constructor() {}

  getAllTiers(): LevelTier[] {
    return this.tiers;
  }

  getTierInfo(tierName: string): LevelTier {
    return this.tiers.find(t => t.name === tierName) || this.tiers[0];
  }

  // =========================================================================
  // ACTIONS & MUTATIONS
  // =========================================================================

  addXp(amount: number, reason: string): { leveledUp: boolean; oldLevel: number; newLevel: number; message: string } {
    const current = this.profile();
    const oldLevel = current.currentLevel;
    const newXp = current.currentXp + amount;

    // Determine new tier & level based on XP
    const newTierObj = this.getTierForXp(newXp);
    const newLevel = newTierObj.level;
    const leveledUp = newLevel > oldLevel;

    // Calculate progress to next level
    const nextTierObj = this.tiers.find(t => t.level === newLevel + 1) || newTierObj;
    const xpSpan = nextTierObj.minXp - newTierObj.minXp;
    const xpIntoTier = Math.max(0, newXp - newTierObj.minXp);
    const progressPercent = xpSpan > 0 ? Math.min(100, Math.round((xpIntoTier / xpSpan) * 100)) : 100;
    const xpToNextLevel = Math.max(0, nextTierObj.minXp - newXp);

    // Update discounts if leveled up
    const unlockedDiscounts = [...current.unlockedDiscounts];
    if (leveledUp && !unlockedDiscounts.some(d => d.code === newTierObj.discountCode)) {
      unlockedDiscounts.push({
        code: newTierObj.discountCode,
        discountPercent: newTierObj.discountPercentage,
        description: `Unlocked at Level ${newTierObj.level} (${newTierObj.title})`
      });
    }

    // Check quest progress
    const updatedQuests = current.quests.map(q => {
      if (q.id === 'quest_xp' && !q.completed) {
        const nextProg = Math.min(q.targetProgress, newXp);
        return { ...q, currentProgress: nextProg, completed: nextProg >= q.targetProgress };
      }
      return q;
    });

    // Check badges
    const updatedBadges = current.badges.map(b => {
      if (b.id === 'platinum_quest' && newXp >= 30000 && !b.unlocked) {
        return { ...b, unlocked: true, unlockedAt: new Date().toISOString() };
      }
      if (b.id === 'loyalty_legend' && newXp >= 20000 && !b.unlocked) {
        return { ...b, unlocked: true, unlockedAt: new Date().toISOString() };
      }
      return b;
    });

    const updatedProfile: GamificationProfile = {
      ...current,
      currentXp: newXp,
      currentLevel: newLevel,
      levelTitle: newTierObj.title,
      tier: newTierObj.name,
      xpToNextLevel,
      progressPercent,
      unlockedDiscounts,
      quests: updatedQuests,
      badges: updatedBadges
    };

    this.profile.set(updatedProfile);
    this.saveProfile(updatedProfile);

    const message = leveledUp
      ? `🎉 LEVEL UP! You reached Level ${newLevel} (${newTierObj.title})! Unlocked perk: ${newTierObj.discountPercentage}% discount code '${newTierObj.discountCode}'.`
      : `+${amount} XP earned for ${reason}. (Total: ${newXp.toLocaleString()} XP)`;

    return { leveledUp, oldLevel, newLevel, message };
  }

  claimQuestReward(questId: string): { success: boolean; rewardText: string } {
    const current = this.profile();
    const quest = current.quests.find(q => q.id === questId);
    if (!quest || !quest.completed || quest.rewardClaimed) {
      return { success: false, rewardText: 'Quest cannot be claimed.' };
    }

    const updatedQuests = current.quests.map(q => {
      if (q.id === questId) {
        return { ...q, rewardClaimed: true };
      }
      return q;
    });

    let rewardText = `Claimed ${quest.rewardValue}!`;
    let updatedProfile = { ...current, quests: updatedQuests };

    if (quest.rewardType === 'XP') {
      const xpVal = parseInt(quest.rewardValue.replace(/[^0-9]/g, '')) || 1000;
      this.profile.set(updatedProfile);
      this.saveProfile(updatedProfile);
      const res = this.addXp(xpVal, `completing '${quest.title}'`);
      return { success: true, rewardText: `${rewardText} ${res.message}` };
    }

    if (quest.rewardType === 'DISCOUNT_VOUCHER') {
      if (!updatedProfile.unlockedDiscounts.some(d => d.code === 'SPECIAL15')) {
        updatedProfile.unlockedDiscounts.push({
          code: 'SPECIAL15',
          discountPercent: 15,
          description: 'Special 15% Quest Completion Voucher'
        });
      }
    }

    this.profile.set(updatedProfile);
    this.saveProfile(updatedProfile);
    return { success: true, rewardText };
  }

  unlockBadge(badgeId: string): Badge | null {
    const current = this.profile();
    const badge = current.badges.find(b => b.id === badgeId);
    if (!badge || badge.unlocked) return null;

    const updatedBadges = current.badges.map(b => {
      if (b.id === badgeId) {
        return { ...b, unlocked: true, unlockedAt: new Date().toISOString() };
      }
      return b;
    });

    const updatedProfile = { ...current, badges: updatedBadges };
    this.profile.set(updatedProfile);
    this.saveProfile(updatedProfile);
    this.addXp(badge.xpReward, `unlocking badge '${badge.title}'`);
    return badge;
  }

  applyFlightBookingGamification(fareAmount: number): { xpEarned: number; leveledUp: boolean; message: string } {
    const earnedXp = Math.round(500 + fareAmount * 0.15);
    const res = this.addXp(earnedXp, 'Booking Commercial Flight');

    // Update first flight and globetrotter quests
    const current = this.profile();
    const updatedQuests = current.quests.map(q => {
      if (q.id === 'quest_flight') {
        const next = Math.min(q.targetProgress, q.currentProgress + 1);
        return { ...q, currentProgress: next, completed: next >= q.targetProgress };
      }
      return q;
    });

    // Ensure First Flight badge is unlocked
    const updatedBadges = current.badges.map(b => {
      if (b.id === 'first_flight' && !b.unlocked) {
        return { ...b, unlocked: true, unlockedAt: new Date().toISOString() };
      }
      return b;
    });

    this.profile.set({ ...current, quests: updatedQuests, badges: updatedBadges });
    this.saveProfile(this.profile());

    return { xpEarned: earnedXp, leveledUp: res.leveledUp, message: res.message };
  }

  applyCheckInGamification(): void {
    this.addXp(250, 'Airport Self Check-In');
    const current = this.profile();
    const updatedQuests = current.quests.map(q => {
      if (q.id === 'quest_checkin') {
        const next = Math.min(q.targetProgress, q.currentProgress + 1);
        return { ...q, currentProgress: next, completed: next >= q.targetProgress };
      }
      return q;
    });
    this.profile.set({ ...current, quests: updatedQuests });
    this.saveProfile(this.profile());
  }

  applyBaggageRadarGamification(): void {
    this.addXp(150, 'Live Baggage Radar Scan');
    const current = this.profile();
    const updatedQuests = current.quests.map(q => {
      if (q.id === 'quest_baggage') {
        return { ...q, currentProgress: 1, completed: true };
      }
      return q;
    });
    this.profile.set({ ...current, quests: updatedQuests });
    this.saveProfile(this.profile());
  }

  // =========================================================================
  // INITIAL DATA & PERSISTENCE
  // =========================================================================

  private getTierForXp(xp: number): LevelTier {
    for (let i = this.tiers.length - 1; i >= 0; i--) {
      if (xp >= this.tiers[i].minXp) {
        return this.tiers[i];
      }
    }
    return this.tiers[0];
  }

  private loadInitialProfile(): GamificationProfile {
    try {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      if (stored) {
        return JSON.parse(stored);
      }
    } catch {
      // Fallback
    }

    // Default Seed (Priya Sharma, Gold Aviator)
    const initialXp = 24850;
    const currentTier = this.tiers[2]; // Gold (15,000 - 30,000)
    const nextTier = this.tiers[3]; // Platinum (30,000)
    const xpSpan = nextTier.minXp - currentTier.minXp;
    const xpIntoTier = initialXp - currentTier.minXp;
    const progressPercent = Math.round((xpIntoTier / xpSpan) * 100);
    const xpToNextLevel = nextTier.minXp - initialXp;

    return {
      userId: 3,
      currentLevel: 3,
      levelTitle: 'Gold Aviator',
      tier: 'GOLD',
      currentXp: initialXp,
      xpToNextLevel,
      progressPercent,
      consecutiveTripStreak: 3,
      badges: [
        {
          id: 'first_flight',
          title: 'First Flight',
          description: 'Booked and flown your first commercial airline reservation with AeroBook.',
          icon: '✈️',
          category: 'TRAVEL',
          unlocked: true,
          unlockedAt: '2026-06-12T09:30:00Z',
          rarity: 'COMMON',
          xpReward: 300
        },
        {
          id: 'globetrotter',
          title: 'Metro Explorer',
          description: 'Flew across 3 unique destination airports (BOM, DEL, BLR).',
          icon: '🌐',
          category: 'EXPLORER',
          unlocked: true,
          unlockedAt: '2026-07-25T14:15:00Z',
          rarity: 'RARE',
          xpReward: 750
        },
        {
          id: 'speedy_checkin',
          title: 'Web Jet Ace',
          description: 'Completed 3 consecutive contactless web check-ins prior to airport arrival.',
          icon: '📱',
          category: 'SPEED',
          unlocked: true,
          unlockedAt: '2026-08-10T11:00:00Z',
          rarity: 'COMMON',
          xpReward: 500
        },
        {
          id: 'pack_master',
          title: 'Pack Master',
          description: 'Checked and tracked baggage on live radar with 0 delivery claims.',
          icon: '🧳',
          category: 'TRAVEL',
          unlocked: true,
          unlockedAt: '2026-08-20T18:00:00Z',
          rarity: 'RARE',
          xpReward: 600
        },
        {
          id: 'sky_lounge',
          title: 'Lounge Connoisseur',
          description: 'Enjoyed complimentary hospitality in an AeroBook Partner VIP lounge.',
          icon: '☕',
          category: 'LOYALTY',
          unlocked: true,
          unlockedAt: '2026-09-05T07:45:00Z',
          rarity: 'EPIC',
          xpReward: 1200
        },
        {
          id: 'loyalty_legend',
          title: 'Loyalty Vanguard',
          description: 'Surpassed 20,000 total career XP in the AeroBook ecosystem.',
          icon: '👑',
          category: 'LOYALTY',
          unlocked: true,
          unlockedAt: '2026-09-18T16:20:00Z',
          rarity: 'EPIC',
          xpReward: 1500
        },
        {
          id: 'platinum_quest',
          title: 'Platinum Horizon',
          description: 'Reach 30,000 XP to enter the prestigious Platinum Jetsetter Tier.',
          icon: '⚡',
          category: 'LOYALTY',
          unlocked: false,
          rarity: 'EPIC',
          xpReward: 2500
        },
        {
          id: 'century_voyage',
          title: 'Aviation Legend',
          description: 'Complete 50 commercial flight legs across domestic and global routes.',
          icon: '💎',
          category: 'EXPLORER',
          unlocked: false,
          rarity: 'LEGENDARY',
          xpReward: 5000
        }
      ],
      quests: [
        {
          id: 'quest_flight',
          title: 'Quarterly Flight Ace',
          description: 'Complete 2 commercial flights during this quarter.',
          currentProgress: 2,
          targetProgress: 2,
          unit: 'flights',
          completed: true,
          rewardType: 'DISCOUNT_VOUCHER',
          rewardValue: '15% Off Flight Voucher (SPECIAL15)',
          rewardClaimed: false
        },
        {
          id: 'quest_checkin',
          title: 'Digital First',
          description: 'Perform web check-in for your reservations online.',
          currentProgress: 3,
          targetProgress: 3,
          unit: 'check-ins',
          completed: true,
          rewardType: 'XP',
          rewardValue: '+500 Bonus XP',
          rewardClaimed: false
        },
        {
          id: 'quest_baggage',
          title: 'Radar Master',
          description: 'Track your checked baggage from counter drop to carousel.',
          currentProgress: 1,
          targetProgress: 1,
          unit: 'radar scan',
          completed: true,
          rewardType: 'XP',
          rewardValue: '+350 Bonus XP',
          rewardClaimed: true
        },
        {
          id: 'quest_streak',
          title: 'Travel Streak',
          description: 'Fly at least once a month for 3 consecutive months.',
          currentProgress: 2,
          targetProgress: 3,
          unit: 'months',
          completed: false,
          rewardType: 'XP',
          rewardValue: '+2,000 Bonus XP & Lounge Pass',
          rewardClaimed: false
        }
      ],
      unlockedDiscounts: [
        { code: 'BRONZE5', discountPercent: 5, description: 'Unlocked at Level 1 (Bronze Wanderer)' },
        { code: 'SILVER10', discountPercent: 10, description: 'Unlocked at Level 2 (Silver Skyfarer)' },
        { code: 'GOLD15', discountPercent: 15, description: 'Unlocked at Level 3 (Gold Aviator)' }
      ]
    };
  }

  private saveProfile(profile: GamificationProfile): void {
    try {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(profile));
    } catch {
      // Storage unavailable
    }
  }
}
