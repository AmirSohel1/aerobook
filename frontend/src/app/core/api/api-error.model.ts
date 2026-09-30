export interface ApiFieldError {
  field: string;
  message: string;
}

export interface ApiErrorPayload {
  timestamp?: string;
  status: number;
  error?: string;
  message: string;
  code?: string;
  fieldErrors?: Record<string, string> | ApiFieldError[];
}

export class ApiError extends Error {
  readonly status: number;
  readonly code: string;
  readonly fieldErrors: Record<string, string>;
  readonly timestamp: string;

  constructor(payload: ApiErrorPayload) {
    super(payload.message || 'An unexpected error occurred');
    this.name = 'ApiError';
    this.status = payload.status || 500;
    this.code = payload.code || payload.error || 'SERVER_ERROR';
    this.timestamp = payload.timestamp || new Date().toISOString();

    if (Array.isArray(payload.fieldErrors)) {
      this.fieldErrors = payload.fieldErrors.reduce((acc, curr) => {
        acc[curr.field] = curr.message;
        return acc;
      }, {} as Record<string, string>);
    } else {
      this.fieldErrors = payload.fieldErrors || {};
    }
  }

  getFieldError(field: string): string | undefined {
    return this.fieldErrors[field];
  }

  hasFieldError(field: string): boolean {
    return !!this.fieldErrors[field];
  }
}
